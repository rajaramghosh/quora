package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;


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

    /**
     * DAO method to get all answers to a question.
     *
     * @param uuid
     * @return
     */
    public List<AnswerEntity> getAllAnswerByQuestionUuid(QuestionEntity question) {

        try {
            return entityManager.createNamedQuery("getAllAnswerByQuestionUuid").setParameter("uuid", question).getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }

}
