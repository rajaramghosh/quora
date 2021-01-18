package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business Service to handle logic for the answers to questions.
 *
 * @author saikatnandi
 */

@Service
public class AnswerBusinessService {

    @Autowired
    UserDao userDao;

    @Autowired
    AnswerDao answerDao;

    @Autowired
    QuestionDao questionDao;

    /**
     * This method is a business service method to save an answer for a particular question.
     *
     * Method checks for the user authentication and the logged in status and throws appropriate errors if validation fails.
     * Method also checks for the validity of the question uuid passed and throws corresponding exceptions if validation fails.
     *
     * @param questionUuid
     * @param answerEntity
     * @param token
     * @return Answer Entity object for the answer created for a particular question
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswerForQuestion(String questionUuid, AnswerEntity answerEntity, String token) throws AuthorizationFailedException, InvalidQuestionException {

        //Check if the user is currently logged in and has a valid access token
        UserAuthEntity userAuth = userDao.getUserAuthByToken(token);
        if (userAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        //Check if the user has been logged out.
        if (userAuth.getLogoutAt() != null && userAuth.getLogoutAt().isAfter(userAuth.getLoginAt())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }

        //Check if the question uuid passed is valid.
        QuestionEntity questionEntity = questionDao.getQuestionById(questionUuid);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        //Setting additional fields for answer entity object passed by the controller.
        answerEntity.setDate(LocalDateTime.now());
        answerEntity.setQuestion(questionEntity);
        answerEntity.setUser(userAuth.getUserId());

        //Save and return the created answer entity object to the controller.
        return answerDao.createAnswerForQuestion(answerEntity);
    }


}
