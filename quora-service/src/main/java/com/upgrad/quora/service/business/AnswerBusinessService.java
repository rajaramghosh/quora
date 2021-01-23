package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * This class implements all the business services for the Answer
 */
@Service
public class AnswerBusinessService {

    @Autowired
    private QuestionBusinessService questionBusinessService;
    @Autowired
    private UserBusinessService userBusinessService;
    @Autowired
    private AnswerDao answerDao;

    /**
     * This method creates answer for a question
     * @param authorizationToken user authorization token
     * @param questionId question is for which answer has to be created
     * @param answerContent content of the answer
     * @return answer details
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final String authorizationToken, String questionId, String answerContent) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthTokenEntity = userBusinessService.getUserByToken(authorizationToken);
        AnswerEntity answerEntity = new AnswerEntity();
        if (userAuthTokenEntity != null) {
            if (userBusinessService.isUserSignedIn(userAuthTokenEntity)) {
                QuestionEntity question  = questionBusinessService.getQuestionById(questionId);
                if (question != null) {
                    answerEntity.setQuestion(question);
                    answerEntity.setDate(ZonedDateTime.now());
                    answerEntity.setAnswer(answerContent);
                    answerEntity.setUuid(UUID.randomUUID().toString());
                    answerEntity.setUser(userAuthTokenEntity.getUserId());
                    answerEntity = answerDao.createAnswer(answerEntity);
                } else {
                    throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        return answerEntity;
    }

    /**
     * This method update an answer
     * @param authorizationToken user authorization token
     * @param answerId answer id
     * @param answerContent answer content
     * @return
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity updateAnswer( String authorizationToken, String answerId, String answerContent) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthTokenEntity = userBusinessService.getUserByToken(authorizationToken);
        if (userAuthTokenEntity != null) {
            if (userBusinessService.isUserSignedIn(userAuthTokenEntity)) {
                AnswerEntity answerEntity = getAnswerForAnswerId(answerId);
                if (answerEntity != null) {
                    if (isUserAnswerOwner(userAuthTokenEntity.getUserId(), answerEntity.getUser())) {
                        answerEntity.setAnswer(answerContent);
                        return answerDao.updateAnswer(answerEntity);
                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
                    }
                } else {
                    throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
            }
        }
        else{
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
    }

    /**
     * This method gets all the answers to a question
     * @param authorizationToken user authorization token
     * @param questionId question id
     * @return list of answers
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    public List<AnswerEntity> getAllAnswersToQuestion(final String authorizationToken, String questionId) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthTokenEntity = userBusinessService.getUserByToken(authorizationToken);
        if (userAuthTokenEntity != null) {
            if (userBusinessService.isUserSignedIn(userAuthTokenEntity)) {
                QuestionEntity questionEntity = questionBusinessService.getQuestionById(questionId);
                if (questionEntity != null) {
                    //Even if the question is valid and there are no answers to the question we are deliberately
                    //not responding with a message that there are no answers as it is not mentioned in requirements
                    return answerDao.getAllAnswersToQuestion(questionEntity.getId());
                } else {
                    throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
    }

    /**
     * This method deletes the answer
     * @param authorizationToken user authorization token
     * @param answerId answer id
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(final String authorizationToken, String answerId) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthTokenEntity = userBusinessService.getUserByToken(authorizationToken);
        if (userAuthTokenEntity != null) {
            if (userBusinessService.isUserSignedIn(userAuthTokenEntity)) {
                AnswerEntity answerEntity = getAnswerForAnswerId(answerId);
                if (answerEntity != null) {
                    if (isUserAnswerOwner(userAuthTokenEntity.getUserId(), answerEntity.getUser())
                            || userBusinessService.isUserAdmin(userAuthTokenEntity.getUserId())) {
                        answerDao.deleteAnswer(answerEntity);
                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
                    }
                } else {
                    throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
    }

    /**
     * This method gets all the answers based on the UUID
     * @param uuid
     * @return Answers
     */
    public AnswerEntity getAnswerForAnswerId(String uuid) {
        return answerDao.getAnswerForAnswerId(uuid);
    }

    /**
     * This method checks if the user is the owner of the answer
     * @param user user details
     * @param answerOwner owner details
     * @return true or false
     */
    public boolean isUserAnswerOwner(UserEntity user, UserEntity answerOwner) {
        boolean isUserAnswerOwner = false;
        if (user != null && answerOwner != null && user.getUuid() != null && !user.getUuid().isEmpty() && answerOwner.getUuid() != null && !answerOwner.getUuid().isEmpty() && user.getUuid().equals(answerOwner.getUuid())) {
            isUserAnswerOwner = true;
            return isUserAnswerOwner;
        }
        return isUserAnswerOwner;
    }
}
