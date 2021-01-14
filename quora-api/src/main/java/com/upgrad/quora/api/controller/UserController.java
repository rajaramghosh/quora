package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * This Controller class for the user related endpoints
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserBusinessService userBusinessService;

    /**
     * The "/user/signup" endpoint is used to register a new user in the Quora Application.
     * It receives a POST request for all the attributes in 'SignupUserRequest' about the user.
     * If the username provided already exists in the current database, throw ‘SignUpRestrictedException’
     * with the message code -'SGR-001' and message - 'Try any other Username, this Username has already been taken'.
     * If the email Id provided by the user already exists in the current database, throw ‘SignUpRestrictedException’
     * with the message code -'SGR-002' and message -'This user has already been registered, try with any other emailId'.
     * Or else save the user information in the database and return the 'uuid' of the registered user and message
     * 'USER SUCCESSFULLY REGISTERED' in the JSON response with the corresponding HTTP status.
     * Also, when a user signs up using this endpoint then the role of the person will be 'nonadmin' by default.
     * @param signupUserRequest User details
     * @return ResponseEntity with HTTP Status Code
     * @throws SignUpRestrictedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        final UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        final UserEntity createdUserEntity = userBusinessService.signup(userEntity);

        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }
}
