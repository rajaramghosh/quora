package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.AdminDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * This class implements the business service for the Admin User
 */
@Service
public class AdminBusinessService {
    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private AdminDao adminDao;

    /**
     * This method checks if the User is an Admin User or Not
     * @param accessToken access token of the user
     * @return if the User is Admin User or not
     * @throws AuthorizationFailedException
     */
    private boolean confirmAdmin(final String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userByToken = userBusinessService.getUserByToken(accessToken);

        if(userByToken.getUserId().getRole().equals("admin"))
            return true;
        else
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
    }

    /**
     * This method allows only an Admin User to delete a User
     * @param accessToken access token of the user
     * @param userId userid of the user
     * @return
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @Transactional
    public String deleteUser(String accessToken, String userId) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userById = userBusinessService.getUserById(userId);
        if(this.confirmAdmin(accessToken)) {
            adminDao.deleteUserByUuid(userId);
        }

        return userId;
    }
}
