package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminService {

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(final UserEntity userEntity) throws SignUpRestrictedException {
        UserEntity userNameEntity = userDao.checkUserName(userEntity.getUsername());
        UserEntity emailEntity = userDao.checkEmailid(userEntity.getEmail());
        // Go for user creation only if username are unique
        if (userNameEntity != null && userEntity.getUsername().equals(userNameEntity.getUsername())) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        // Go for user creation only if email id are unique
        if (emailEntity != null && userEntity.getEmail().equals(emailEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        String password = userEntity.getPassword();
        if (password == null) {
            userEntity.setPassword("proman@123");
        }
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
    }
}
