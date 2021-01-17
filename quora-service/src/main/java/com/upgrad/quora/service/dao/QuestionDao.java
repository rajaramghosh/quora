package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

// Class implements methods to access question table through entity manager

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    //Method to create question and invokes persist entityManager
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        try {
            entityManager.persist(questionEntity);
            return questionEntity;
        } catch (Exception e) {
            return null;
        }
    }

    //Method to delete question based on UUID
    public void deleteQuestion(final String uuid) {
        try {
            entityManager.createNamedQuery("deleteQuestionById")
                    .setParameter("uuid", uuid)
                    .executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //Method to edit question based on UUID
    public void editQuestion(String uuid, String content) {
        try {
            entityManager.createNamedQuery("editQuestionById")
                    .setParameter("uuid", uuid)
                    .setParameter("content", content)
                    .executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //Method to view all questions using named query
    public List<QuestionEntity> getAllQuestions() {
        return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
    }

    //Method to view question based on UUID using named query
    public QuestionEntity getQuestionById(final String id) {
        try {
            return entityManager.createNamedQuery("getQuestionById", QuestionEntity.class).setParameter("uuid", id).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //Method to view all questions based on userid using named query
    public List<QuestionEntity> getAllQuestionsByUser(final UserEntity id) {
        List questionsList = entityManager.createNamedQuery("getAllQuestionsByUser").setParameter("user_id", id).getResultList();
        System.out.println(questionsList.size());
        return questionsList;
    }

}
