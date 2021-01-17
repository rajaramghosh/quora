package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

// Class implements methods to access question table data

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        try {
            entityManager.persist(questionEntity);
            return questionEntity;
        } catch (Exception e) {
            return null;
        }
    }
    public void deleteQuestion(final String uuid) {
        try {
            entityManager.createNamedQuery("deleteQuestionById")
                    .setParameter("uuid", uuid)
                    .executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<QuestionEntity> getAllQuestions() {
    return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
    }



}
