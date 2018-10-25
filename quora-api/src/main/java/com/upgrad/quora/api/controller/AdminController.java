package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.UserAdminService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Controller class used for invoking admin Rest endpoints
 */
@RestController
@RequestMapping("/")
public class AdminController {

    //constant messages
    private static final String USER_SUCCESSFULLY_DELETED = "USER SUCCESSFULLY DELETED";

    @Autowired
    UserAdminService userAdminService;

    /**
     * Rest Endpoint method implementation used for deleting user details from database.
     *
     * @param userUuid      user uuid to be deleted
     * @param authorization authorization token of the user.
     * @return ResponseEntity of type UserDeleteResponse
     * @throws AuthorizationFailedException exception thrown when user
     * @throws UserNotFoundException
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        //Check user to be deleted
        final UserEntity userEntity = userAdminService.getUser(userUuid, authorization, true);
        //send the user for deletion
        userAdminService.deleteUser(userUuid);
        //send REST response to client
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(userEntity.getUuid()).status(USER_SUCCESSFULLY_DELETED);
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }


}
