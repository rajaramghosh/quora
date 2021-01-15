package com.upgrad.quora.service.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This class implements methods to access the database
 */
@Repository
public class AdminDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method deletes a User from the database based on the UUID
     * @param uuid UUID of the User
     */
    public void deleteUserByUuid(final String uuid) {
        try {
            entityManager.createNamedQuery("deleteUserById")
                    .setParameter("uuid", uuid)
                    .executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
