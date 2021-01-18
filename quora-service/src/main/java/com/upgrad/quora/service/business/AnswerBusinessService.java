package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * This method is a business service method to get all answers for a particular question.
     *
     * Method checks for the user authentication and the logged in status and throws appropriate errors if validation fails.
     * Method also checks for the validity of the question uuid passed and throws corresponding exceptions if validation fails.
     *
     * @param questionUuid
     * @param token
     * @return List of Answer Entity object for the answers for a particular question
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAllAnswerForQuestion(String questionUuid, String token) throws AuthorizationFailedException, InvalidQuestionException {

        //Check if the user is currently logged in and has a valid access token
        UserAuthEntity userAuth = userDao.getUserAuthByToken(token);
        if (userAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        //Check if the user has been logged out.
        if (userAuth.getLogoutAt() != null && userAuth.getLogoutAt().isAfter(userAuth.getLoginAt())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
        }

        //Check if the question uuid passed is valid.
        QuestionEntity questionEntity = questionDao.getQuestionById(questionUuid);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        // call to Dao to get all the answers for the question
        List<AnswerEntity> answers = answerDao.getAllAnswerByQuestionUuid(questionEntity);

        //return the list of answers.
        return answers;

    }

    /**
     * This method is a business service method to get all answers for a particular question.
     *
     * Method checks for the user authentication and the logged in status and throws appropriate errors if validation fails.
     * Method also checks for the validity of the answer uuid passed and throws corresponding exceptions if validation fails.
     * Method also checks if the owner of the answer is editing the answer and throws authorization exception as appropriate.
     *
     * @param answerUuid
     * @param content
     * @param token
     * @return answer entity of the edited answer
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(String answerUuid, String content, String token) throws AuthorizationFailedException, AnswerNotFoundException {

        //Check if the user is currently logged in and has a valid access token
        UserAuthEntity userAuth = userDao.getUserAuthByToken(token);
        if (userAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        //Check if the user has been logged out.
        if (userAuth.getLogoutAt() != null && userAuth.getLogoutAt().isAfter(userAuth.getLoginAt())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the answer");
        }

        //Check if the answer uuid passed is valid.
        AnswerEntity answerEntity = answerDao.getAnswerByUuid(answerUuid);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        //Check if the owner of the answer is editing the answer content.
        if (!answerEntity.getUser().getUuid().equals(userAuth.getUserId().getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        //Setting additional fields in answer entity passed from controller after validations.
        answerEntity.setAnswer(content);
        answerEntity.setDate(LocalDateTime.now());

        //call to Dao to save the changes and return the updated entity.
        return answerDao.editAnswer(answerEntity);

    }

    /**
     * This method is a business service method to get all answers for a particular question.
     *
     * Method checks for the user authentication and the logged in status and throws appropriate errors if validation fails.
     * Method also checks for the validity of the answer uuid passed and throws corresponding exceptions if validation fails.
     * Method also checks if the owner of the answer is deleting the answer and throws authorization exception as appropriate.
     *
     * @param ansUuid
     * @param token
     * @return AnswerEntity
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(String ansUuid, String token) throws AuthorizationFailedException, AnswerNotFoundException {

        //Check if the user is currently logged in and has a valid access token
        UserAuthEntity userAuth = userDao.getUserAuthByToken(token);
        if (userAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        //Check if the user has been logged out.
        if (userAuth.getLogoutAt() != null && userAuth.getLogoutAt().isAfter(userAuth.getLoginAt())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete the answer");
        }

        //Check if the answer uuid passed is valid.
        AnswerEntity answerEntity = answerDao.getAnswerByUuid(ansUuid);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        //Check if the owner of the answer or admin is deleting the answer content.
        if (!answerEntity.getUser().getUuid().equals(userAuth.getUserId().getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
        return answerDao.deleteAnswer(answerEntity);

    }

}
