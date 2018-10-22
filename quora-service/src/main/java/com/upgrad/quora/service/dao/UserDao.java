package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

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
}
