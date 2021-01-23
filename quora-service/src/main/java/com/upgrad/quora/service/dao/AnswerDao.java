package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

/**
 * This class implements methods to access the database
 */
@Repository
public class AnswerDao {
    @Autowired
    EntityManager entityManager;

    /**
     * This method create an answer
     * @param answerEntity answer details
     * @return answer details
     */
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        this.entityManager.persist(answerEntity);
        return answerEntity;
    }

    /**
     * This method update the answer
     * @param answerEntity answer details
     * @return answer details
     */
    public AnswerEntity updateAnswer(AnswerEntity answerEntity) {
        return entityManager.merge(answerEntity);
    }

    /**
     * This method deletes the answer
     * @param answerEntity answer details
     */
    public void deleteAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
    }

    /**
     * This method get all the answers for a question
     * @param id id
     * @return list of answers
     */
    public List<AnswerEntity> getAllAnswersToQuestion(int id) {
        try {
            return this.entityManager.createNamedQuery("getAnswersForQuestionId", AnswerEntity.class).setParameter("uuid", id).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method retrieves the answer
     * @param uuid uuid
     * @return answer details
     */
    public AnswerEntity getAnswerForAnswerId(String uuid) {
        try {
            return this.entityManager.createNamedQuery("getAnswerForAnswerId", AnswerEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
