package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * DAO class to handle persistence of answer related jobs into the DB .
 *
 * @author saikatnandi
 */

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * DAO method to persist the new answer to a particular question
     * @param answerEntity
     * @return saved answerEntity
     */
    public AnswerEntity createAnswerForQuestion(AnswerEntity answerEntity) {

        entityManager.persist(answerEntity);
        return answerEntity;
    }

}
