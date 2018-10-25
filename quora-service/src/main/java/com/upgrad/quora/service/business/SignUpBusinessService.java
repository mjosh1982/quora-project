package com.upgrad.quora.service.business;

import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class used for user creation.
 */
@Service
public class SignUpBusinessService {

    @Autowired
    UserAdminService userAdminService;


    /**
     * Method used to add a user.
     *
     * @param userEntity user to added to database
     * @return User object created
     * @throws SignUpRestrictedException exception thrown in case username of email id are same.
     */
    public UserEntity signUp(UserEntity userEntity) throws SignUpRestrictedException {
        return userAdminService.createUser(userEntity);
    }


}
