package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;

/**
 * This class implements methods to access the database
 */
@Repository
public class UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method will insert a new user in the database
     * @param userEntity user details to be inserted in the database
     * @return user details inserted in the database
     */
    public UserEntity createUser(UserEntity userEntity) {
        try {
            entityManager.persist(userEntity);
            return userEntity;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * This method checks if the username already exist in the database
     * @param userName username that need to be checked
     * @return true/false
     */
    public Boolean isUserNameExists(final String userName) {
        try {
            UserEntity singleResult = entityManager.createNamedQuery("getUserByUserName", UserEntity.class).setParameter("userName", userName).getSingleResult();
            return true;
        } catch (NoResultException nre) {
            return false;
        }
    }

    /**
     * This method checks if the email already exist in the database
     * @param email email that need to be checked
     * @return true/false
     */
    public Boolean isEmailExists(final String email) {
        try {
            UserEntity singleResult = entityManager.createNamedQuery("getUserByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
            return true;
        } catch (NoResultException nre) {
            return false;
        }
    }

    /**
     * This method check if a user with the given userName is resent in the Database
     * @param userName user name to be found
     * @return null if not found or returns the User Details
     */
    public UserEntity getUserByUserName(final String userName) {
        try {
            return entityManager.createNamedQuery("getUserByUserName", UserEntity.class).setParameter("userName", userName).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method insert the user authentication data in the database
     * @param userAuthEntity user authentication data
     * @return user authentication data
     */
    public UserAuthEntity createAuth(final UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    /**
     * This method get the user details based on the access token
     * @param accessToken access toke of the user
     * @return null if not found or else the user details
     */
    public UserAuthEntity getUserAuthByToken(final String accessToken) {
        try {
            UserAuthEntity authEntity = entityManager.createNamedQuery("userAuthByToken", UserAuthEntity.class)
                    .setParameter("token", accessToken)
                    .getSingleResult();

            return authEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * The method Update the user_auth table when the on successful sign out
     * @param accessToken access token of the user
     * @param logoutAt sign out time
     */
    public void updateUserLogoutByToken(final String accessToken, final ZonedDateTime logoutAt) {
        entityManager.createNamedQuery("updateLogoutByToken" )
                .setParameter("token", accessToken)
                .setParameter("logoutAt", logoutAt)
                .executeUpdate();
    }
}
