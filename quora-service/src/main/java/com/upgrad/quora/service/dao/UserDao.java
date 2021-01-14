package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

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
     * @param email emil that need to be checked
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
}
