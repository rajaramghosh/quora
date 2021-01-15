package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This class implements the controller for the end points userProfile - "/userprofile/{userId}"
 */
@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private UserBusinessService userBusinessService;

    /**
     * The "/userprofile/{userId}" endpoint is used to get the details of any user in the Quora Application.
     * This endpoint can be accessed by any user in the application.
     * @param userUuid uuid of the user
     * @param authorization authorization code of the user
     * @return appropriate response entity
     * @throws UserNotFoundException
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}")
    public ResponseEntity<UserDetailsResponse> getUserById(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String authorization) throws UserNotFoundException, AuthorizationFailedException {

        UserEntity userById = userBusinessService.getUserProfile(userUuid, authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();

        userDetailsResponse.setFirstName(userById.getFirstName());
        userDetailsResponse.setLastName(userById.getLastName());
        userDetailsResponse.setUserName(userById.getUserName());
        userDetailsResponse.setEmailAddress(userById.getEmail());
        userDetailsResponse.setCountry(userById.getCountry());
        userDetailsResponse.setAboutMe(userById.getAboutMe());
        userDetailsResponse.setContactNumber(userById.getContactNumber());
        userDetailsResponse.setDob(userById.getDob());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK) ;
    }
}
