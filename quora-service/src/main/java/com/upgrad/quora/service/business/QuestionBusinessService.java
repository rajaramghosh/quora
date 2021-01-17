package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserBusinessService userBusinessService;

    //Method to create the question
    @Transactional
    public QuestionEntity create(QuestionEntity questionEntity)  {
        QuestionEntity createQuestion = questionDao.createQuestion(questionEntity);
        return createQuestion;
    }

    //Method to delete the question associated based on UUID and throw necessary exceptions
    @Transactional
    public void deleteQuestion(final String uuid,final String authorization) throws AuthorizationFailedException, InvalidQuestionException  {
        UserAuthEntity user = userBusinessService.getUserByToken(authorization);
        QuestionEntity question = getQuestionById(uuid);
        UserEntity questionUserId=question.getUserId();
        UserEntity authUserID=user.getUserId();
        String authUserRole=user.getUserId().getRole();
        if(questionUserId==authUserID || authUserRole.equals("admin") ) {
            questionDao.deleteQuestion(uuid);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
    }

    //Method to edit the question associated based on UUID and throw necessary exceptions
    @Transactional
    public String editQuestion(final String uuid, final String questionContent,final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity user = userBusinessService.getUserByToken(authorization);
        QuestionEntity question = getQuestionById(uuid);
        UserEntity questionUserId=question.getUserId();
        UserEntity authUserID=user.getUserId();
        if(questionUserId==authUserID) {
            questionDao.editQuestion(uuid, questionContent);
            return uuid;
        }
        else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
    }

    //Method to view Question based on UUID and throw necessary exceptions
    public QuestionEntity getQuestionById(String id) throws InvalidQuestionException {
        QuestionEntity question = questionDao.getQuestionById(id);
        if(question == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        return question;
    }

    //Method to view all questions and throw necessary exceptions
    public List<QuestionEntity> getAllQuestions(final String authorization) throws AuthorizationFailedException {
        UserAuthEntity user = userBusinessService.getUserByToken(authorization);
        return questionDao.getAllQuestions();
    }

    //Method to view all questions associated to a user and throw necessary exceptions
    public List<QuestionEntity> getAllQuestionsByUser(final UserEntity userId, final String authorization)  throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity user = userBusinessService.getUserByToken(authorization);
        List<QuestionEntity> questionsList = questionDao.getAllQuestionsByUser(userId);
        if (questionsList.size()>0) {
            return questionsList;
        }
        else {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

    }

}
