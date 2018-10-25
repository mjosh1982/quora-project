package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.type.ActionType;
import com.upgrad.quora.service.type.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class used for user related operations.
 */
@Service
public class UserAdminService {

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    private UserDao userDao;

    /**
     * Method used for creating a new user.
     * It checks for uniques username and email. Throws appropriate exceptions if either of them is not unique.
     * The password is encrypted and saved.
     *
     * @param userEntity user object to be created
     * @return Createdd user object
     * @throws SignUpRestrictedException exception thrown in case of non-unique username or email.
     */
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

        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
    }


    /**
     * Method used to get the user details.
     * The user asking for details is validated with his authorization token.
     * If singed in, then the user is provided with the user details.
     *
     * @param userUuid           user uuid
     * @param authorizationToken logged in used authorization token
     * @param admin              boolean indicating whether is coming from admin or nonadmin purpose.
     * @return User details
     * @throws UserNotFoundException        thrown if user is not found
     * @throws AuthorizationFailedException If the logged in user is not authorized to see the details.
     */
    public UserEntity getUser(String userUuid, String authorizationToken, boolean admin) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        if (admin) {
            //If role of user is admin then check if the requesting user has admin role.If not then throw exception.
            if (userAuthTokenEntity.getUser().getRole().equals(RoleType.nonadmin.toString())) {
                throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
            }
        }

        //if no auth token throw exception
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        //if user has signed out throw exception
        if (userAuthTokenEntity.getLogoutAt() != null) {
            if (userAuthTokenEntity.getUser().getRole().equals(RoleType.admin.toString())) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.");
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }
        UserEntity userEntity = userDao.getUser(userUuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        } else {
            return userEntity;
        }
    }


    /**
     * Method used for deleting user from database.
     * If user is not available then it will throw exception.
     *
     * @param userUuid uuid of the user logged in
     * @throws UserNotFoundException Exception thrown if user to be deleted is not found in database
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(String userUuid) throws UserNotFoundException {

        UserEntity user = userDao.getUser(userUuid);
        if (user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        } else {
            userDao.deleteUser(user);
        }
    }

    /**
     * method used for get user auth details for the user with right access token.
     *
     * @param authorizationToken access token value
     * @param actionType         action type based on which we have to send various messages
     * @return UserAuth Entity object
     * @throws AuthorizationFailedException exception indicating user is not signed in or not signed out.
     */
    public UserAuthEntity getUserByAccessToken(String authorizationToken, ActionType actionType) throws AuthorizationFailedException {
        UserAuthEntity userAuthTokenEntity = userDao.checkaAuthToken(authorizationToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthTokenEntity.getLogoutAt() != null) {
            if (actionType.equals(ActionType.CREATE)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
            } else if (actionType.equals(ActionType.ALL_QUESTION)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
            } else if (actionType.equals(ActionType.EDIT)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
            } else if (actionType.equals(ActionType.DELETE)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
            } else if (actionType.equals(ActionType.ALL_FOR_USER)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
            }
        }
        return userAuthTokenEntity;
    }


}
