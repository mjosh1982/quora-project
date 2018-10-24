package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/**
 * Service class for providing authentication funtionality.
 */
@Service
public class AuthenticationService {

    @Autowired
    UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * Method is used for providing authentication to a user
     * The method takes username and password as argument.
     *
     * @param username
     * @param password
     * @return
     * @throws AuthenticationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUserName(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        String encryptedPwd = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());
        if (encryptedPwd.equals(userEntity.getPassword())) {
            JwtTokenProvider tokenProvider = new JwtTokenProvider(encryptedPwd);
            UserAuthEntity userAuthToken = new UserAuthEntity();
            userAuthToken.setUser(userEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthToken.setAccessToken(tokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);
            ///userAuthToken.setCreatedBy("api-backend");
            //userAuthToken.setCreatedAt(now);
            //userDao.createAuthToken(userAuthToken);
            //userEntity.setLastLoginAt(now);
            return userAuthToken;

        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }
}
