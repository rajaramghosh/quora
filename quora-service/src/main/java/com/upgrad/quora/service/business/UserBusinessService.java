package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * This class implement all the business services for the User
 */
@Service
public class UserBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    /**
     * This method checks if the username or the email already exist in the database then throw appropriate exception or else
     * create the new user with the provided details
     * @param userEntity
     * @return
     * @throws SignUpRestrictedException
     */
    @Transactional
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        if(userDao.isUserNameExists(userEntity.getUserName())) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        if(userDao.isEmailExists(userEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        UserEntity signupUser = userDao.createUser(userEntity);

        return signupUser;
    }

    /**
     * This method checks the username and password to sign in a valid user or reply with appropriate
     * message
     * @param username username of the user
     * @param password password of the user
     * @return User details on success or appropriate error message
     * @throws AuthenticationFailedException
     */
    @Transactional
    public UserAuthEntity signin(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUserName(username);

        if(userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        if(encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthEntity = new UserAuthEntity();

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuthEntity.setUuid(UUID.randomUUID().toString());
            userAuthEntity.setUserId(userEntity);
            userAuthEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthEntity.setExpiresAt(expiresAt);
            userAuthEntity.setLoginAt(now);

            userDao.createAuth(userAuthEntity);

            return userAuthEntity;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }

    }

    /**
     * This method sign out a user if already signed in or respond with appropriate message
     * @param accessToken of the logged in user
     * @return message based on the user state
     * @throws SignOutRestrictedException
     */
    @Transactional
    public String signout(final String accessToken) throws SignOutRestrictedException {
        ZonedDateTime currentTime = ZonedDateTime.now();
        UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(accessToken);

        if(userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

        userDao.updateUserLogoutByToken(accessToken, currentTime);

        return userAuthEntity.getUserId().getUuid();
    }
}
