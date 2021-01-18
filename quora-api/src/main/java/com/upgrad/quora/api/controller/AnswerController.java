package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller class to handle all requests for answers
 *
 * @author saikatnandi
 */

@RestController
@RequestMapping(path = "/answer")
public class AnswerController {

    @Autowired
    AnswerBusinessService ansService;

    @Autowired
    QuestionBusinessService questionService;

    /**
     * The endpoint, /question/{questionId}/answer/create, is used to create an answer to a particular question. Any user can access this endpoint.
     *
     * It should be a POST request.
     * This endpoint requests for the attribute in "Answer Request", the path variable 'questionId ' as a string for the
     * corresponding question which is to be answered in the database and access token of the signed in user as a string
     * in authorization Request Header.
     *
     * If the question uuid entered by the user whose answer is to be posted does not exist in the database,
     * throw "InvalidQuestionException" with the message code - 'QUES-001' and message - 'The question entered is invalid'.
     *
     * If the access token provided by the user does not exist in the database throw "AuthorizationFailedException"
     * with the message code - 'ATHR-001' and message - 'User has not signed in'.
     *
     * If the user has signed out, throw "AuthorizationFailedException" with the message code - 'ATHR-002'
     * and message - 'User is signed out.Sign in first to post an answer'.
     *
     * Else, save the answer information in the database and return the "uuid" of the answer and message "ANSWER CREATED"
     * in the JSON response with the corresponding HTTP status.
     *
     * @param authorization
     * @param quesUuid
     * @param answerRequest
     * @return create answer response with appropriate message
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @RequestMapping(path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String quesUuid, final AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {

        //Creating AnswerEntity from the AnswerRequest data provided by the front end.
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setUuid(UUID.randomUUID().toString());

        //Business service calls to save the answer to a question.
        ansService.createAnswerForQuestion(quesUuid, answerEntity, authorization);

        //Build response for front end after persisting the answer
        AnswerResponse answerRsp = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerRsp, HttpStatus.CREATED);

    }

}