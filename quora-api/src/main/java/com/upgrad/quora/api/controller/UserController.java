package com.upgrad.quora.api.controller;


import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.SignUpBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.type.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;

import java.util.Base64;
import java.util.UUID;

/**
 * Controller class for defining rest API endpoints for
 */
@RestController
@RequestMapping("/")
public class UserController {

    //Constant for messages
    private static final String USER_SUCCESSFULLY_REGISTERED = "USER SUCCESSFULLY REGISTERED";
    private static final String SIGNIN_MESSAGE = "SIGNED IN SUCCESSFULLY";
    private static final String SIGNED_OUT_SUCCESSFULLY = "SIGNED OUT SUCCESSFULLY";

    @Autowired
    SignUpBusinessService signUpBusinessService;

    @Autowired
    AuthenticationService authenticationService;


    /**
     * Rest Endpoint method implementation used for signing up user with all details.
     * Since, the user is added through REST API, the role type would be nonadmin
     *
     * @param signupUserRequest request object containing user details.
     * @return ResponseEntity containing user response
     * @throws SignUpRestrictedException exception thrown in case username of email id are same.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signUp(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUsername(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setRole(RoleType.getEnum(1).toString());
        UserEntity createdUserEntity = signUpBusinessService.signUp(userEntity);
        //Here since the json provided does not have message variable, hence we cannot show message in the response
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status(USER_SUCCESSFULLY_REGISTERED);
        ResponseEntity<SignupUserResponse> response = new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
        return response;
    }

    /**
     * Rest Endpoint method implementation  used to signin a user into the system.
     * The user is first authenticated with his username and password.
     * Then, user auth token is created and with this auth token user
     * is given access to the application.
     *
     * @param authorization authorization string provided in the format "Basic <BASE64 encoded value>"
     * @return ResponseEntity providing signinresponse object
     * @throws AuthenticationFailedException if user is not authenticated then this exception is thrown
     */

    @RequestMapping(method = RequestMethod.POST, path = "user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader final String authorization) throws AuthenticationFailedException {
        //Basic dXNlcm5hbWU6cGFzc3dvcmQ=
        //above is a sample encoded text where the username is "username" and password is "password" seperated by a ":"
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        UserAuthEntity userAuthToken = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
        UserEntity user = userAuthToken.getUser();
        SigninResponse authorizedUserResponse = new SigninResponse().id(user.getUuid()).
                message(SIGNIN_MESSAGE);
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(authorizedUserResponse, headers, HttpStatus.OK);
    }


    /**
     * Rest Endpoint method implementation used for signing out user using the access token passed as parameter.
     * If access token is valid or available then SignOutRestrictedException is thrown.
     *
     * @param accessToken accesstoken passed as String
     * @return ResponseEntity object containing SignoutResponse object
     * @throws SignOutRestrictedException exception thrown in case of no acess token found.
     */
    @RequestMapping(method = RequestMethod.POST, path = "user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader final String accessToken) throws SignOutRestrictedException {
        UserEntity userEntity = authenticationService.authenticateAccessToken(accessToken);
        SignoutResponse signOutResponse = new SignoutResponse().id(userEntity.getUuid()).message(SIGNED_OUT_SUCCESSFULLY);
        return new ResponseEntity<SignoutResponse>(signOutResponse, HttpStatus.OK);
    }


}
