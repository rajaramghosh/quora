package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
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

    /**
     * The "/user/signin" endpoint is used for user authentication. The user authenticates in the application and after
     * successful authentication, JWT token is given to a user.
     * It is a POST request for the User credentials to be passed in the authorization field of header as part of
     * Basic authentication, "Basic username:password" (where username:password of the String is encoded
     * to Base64 format) in the authorization header.
     * If the username provided by the user does not exist, throw "AuthenticationFailedException" with the message code
     * -'ATH-001' and message-'This username does not exist'.
     * If the password provided by the user does not match the password in the existing database,
     * throw 'AuthenticationFailedException' with the message code -'ATH-002' and message -'Password failed'.
     * If the credentials provided by the user match the details in the database, save the user login information in the
     * database and return the 'uuid' of the authenticated user from 'users' table and message 'SIGNED IN SUCCESSFULLY' in
     * the JSON response with the corresponding HTTP status. Note that 'JwtAccessToken' class has been given in the stub file
     * to generate an access token.
     * Also, return the access token in the access_token field of the Response Header, which will be used by the user for
     * any further operation in the Quora Application.
     * @param authorization authorization details
     * @return signin response with appropriate message
     * @throws AuthenticationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decoded = Base64.getDecoder().decode(authorization.split(" ")[1]);
        String decodedText = new String(decoded);
        String[] decodedArray = decodedText.split(":");

        UserAuthEntity authEntity = userBusinessService.signin(decodedArray[0], decodedArray[1]);
        SigninResponse signinResponse = new SigninResponse();
        signinResponse.setId(authEntity.getUserId().getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token", authEntity.getAccessToken());

        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK );
    }

    /**
     * The "/user/signout" endpoint is used to sign out from the Quora Application. The user cannot access any other
     * endpoint once he is signed out of the application.
     * It should be a POST request. This endpoint must request the access token of the signed in user in the
     * authorization field of the Request Header.
     * If the access token provided by the user does not exist in the database, throw 'SignOutRestrictedException' with
     * the message code -'SGR-001' and message - 'User is not Signed in'.
     * If the access token provided by the user is valid, update the LogoutAt time of the user in the database and
     * return the 'uuid' of the signed out user from 'users' table and message 'SIGNED OUT SUCCESSFULLY' in the JSON
     * response with the corresponding HTTP status.
     * @param accessToken access token of the user
     * @return appropriate message based on the state of the user
     * @throws SignOutRestrictedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "signout")
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String accessToken) throws SignOutRestrictedException {

        String signedOutUser = userBusinessService.signout(accessToken);
        SignoutResponse signoutResponse = new SignoutResponse().id(signedOutUser).message("SIGNED OUT SUCCESSFULLY");

        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }
}
