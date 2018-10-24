package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * DAO class for providing user related database trasactions.
 */
@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * This method is used for creating a new user.
     *
     * @param userEntity User object
     * @return created user Object
     */
    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    /**
     * This method is used for checking whether the userName used for
     * signup is already taken. If yes, then it will throw SignUpRestrictedException
     *
     * @param username userName used for signUp
     * @return boolean indicating whether the userName exists or not.
     * @throws SignUpRestrictedException exception to be propagated above
     */
    public UserEntity checkUserName(final String username) {
        try {
            return entityManager.createNamedQuery("userByUserName", UserEntity.class).setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    /**
     * This method is used for checking whether the emailId used for
     * signup is already taken. If yes, then it will throw SignUpRestrictedException
     *
     * @param emailid userName used for signUp
     * @return boolean indicating whether the userName exists or not.
     * @throws SignUpRestrictedException exception to be propagated above
     */
    public UserEntity checkEmailid(String emailid) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", emailid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public UserAuthEntity getUserAuthToken(final String authorizationToken) {
        try {
            return entityManager
                    .createNamedQuery("userAuthTokenByAccessToken", UserAuthEntity.class).setParameter("accessToken", authorizationToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * method to get user Entity by uuid.
     *
     * @param userUUid uuid of the user
     * @return UserEntity object
     */
    public UserEntity getUser(String userUUid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userUUid).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * method to get the user by username
     *
     * @param username username as String value
     * @return UserEntity Object
     */
    public UserEntity getUserByUserName(String username) {
        try {
            return entityManager.createNamedQuery("userByUserName", UserEntity.class).setParameter("username", username).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
