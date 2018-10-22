package com.upgrad.quora.service.business;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignUpBusinessService {

    @Autowired
    UserAdminService userAdminService;

    public UserEntity signUp(UserEntity userEntity){
        return userAdminService.createUser(userEntity);
    }


}
