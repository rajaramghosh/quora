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

import java.util.List;
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
     * This is a POST request endpoint, /question/{questionId}/answer/create, that is used to create an answer to a particular question. Any user can access this endpoint.
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

    /**
     * This is a GET request endpoint, answer/all/{questionId}, that is used to get all answers to a particular question. Any user can access this endpoint.
     *
     * This endpoint requests the path variable 'questionId' as a string for the corresponding question whose answers
     * are to be retrieved from the database and access token of the signed in user as a string in authorization Request Header.
     *
     * If the access token provided by the user does not exist in the database throw "AuthorizationFailedException"
     * with the message code - 'ATHR-001' and message - 'User has not signed in'.
     *
     * If the user has signed out, throw "AuthorizationFailedException" with the message code - 'ATHR-002'
     * and message - 'User is signed out.Sign in first to get the answers'.
     *
     * If the question with uuid whose answers are to be retrieved from the database does not exist in the database,
     * throw "InvalidQuestionException" with the message code - 'QUES-001'
     * and message - 'The question with entered uuid whose details are to be seen does not exist'.
     *
     * Else, return "uuid" of the answer, "content" of the question and "content" of all the answers posted
     * for that particular question from the database in the JSON response with the corresponding HTTP status.
     *
     * @param authorization
     * @param quesUuid
     * @return create answer details response with appropriate messages
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @RequestMapping(path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public ResponseEntity<AnswerDetailsResponse> getAllAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String quesUuid) throws AuthorizationFailedException, InvalidQuestionException {

        //getting the list of all answer entities for the question uuid
        List<AnswerEntity> ansEntity = ansService.getAllAnswerForQuestion(quesUuid, authorization);

        // getting the question entity for the question uuid.
        QuestionEntity quesEntity = questionService.getQuestionById(quesUuid);

        // creating a single string of all answers separated by "|"
        StringBuffer answers = new StringBuffer();
        for(AnswerEntity ans: ansEntity) {
            answers.append(ans.getAnswer());
            answers.append("|");
        }

        // building the answer details response
        AnswerDetailsResponse answerRsp = new AnswerDetailsResponse().id(quesUuid)
                .questionContent(quesEntity.getContent())
                .answerContent(answers.substring(0, answers.length()-1));

        return new ResponseEntity<>(answerRsp, HttpStatus.OK);

    }

    /**
     * This is a PUT request endpoint, /answer/edit/{answerId}, that is used to edit an answer.
     *
     * This endpoint requests for all the attributes in "AnswerEditRequest", the path variable 'answerId' as a string
     * for the corresponding answer which is to be edited in the database and access token of the
     * signed in user as a string in authorization Request Header.
     *
     * If the access token provided by the user does not exist in the database throw "AuthorizationFailedException"
     * with the message code - 'ATHR-001' and message - 'User has not signed in'.
     *
     * If the user has signed out, throw "AuthorizationFailedException" with the message code - 'ATHR-002'
     * and message 'User is signed out.Sign in first to edit an answer'.
     *
     * Only the answer owner can edit the answer. Therefore, if the user who is not the owner of the answer tries to
     * edit the answer throw "AuthorizationFailedException" with the message code - 'ATHR-003' and
     * message - 'Only the answer owner can edit the answer'.
     *
     * If the answer with uuid which is to be edited does not exist in the database, throw "AnswerNotFoundException"
     * with the message code - 'ANS-001' and message - 'Entered answer uuid does not exist'.
     *
     * Else, edit the answer in the database and return "uuid" of the edited answer and message "ANSWER EDITED" in the
     * JSON response with the corresponding HTTP status.
     *
     * @param authorization
     * @param ansUuid
     * @param answerRequest
     * @return create answer edit reponse with appropriate messages
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @RequestMapping(path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("answerId") final String ansUuid, final AnswerRequest answerRequest) throws AuthorizationFailedException, AnswerNotFoundException {

        // business service call to edit the answer.
        ansService.editAnswer(ansUuid, answerRequest.getAnswer(), authorization);

        //create the answer edit response.
        AnswerEditResponse answerRsp = new AnswerEditResponse().id(ansUuid).status("ANSWER EDITED");

        return new ResponseEntity<>(answerRsp, HttpStatus.OK);

    }

    /**
     * This is a DELETE request endpoint, /answer/delete/{answerId}, that is used to delete an answer.
     *
     * This endpoint requests for the path variable 'answerId' as a string for the corresponding answer which is to be
     * deleted from the database and access token of the signed in user as a string in authorization Request Header.
     *
     * If the access token provided by the user does not exist in the database throw "AuthorizationFailedException"
     * with the message code - 'ATHR-001' and message - 'User has not signed in'.
     *
     * If the user has signed out, throw "AuthorizationFailedException" with the message code - 'ATHR-002'
     * and message - 'User is signed out.Sign in first to delete an answer'.
     *
     * Only the answer owner or admin can delete the answer. Therefore, if the user who is not the owner of the answer
     * or the role of the user is ‘nonadmin’ and tries to delete the answer throw "AuthorizationFailedException"
     * with the message code - 'ATHR-003' and message - 'Only the answer owner or admin can delete the answer'.
     *
     * If the answer with uuid which is to be deleted does not exist in the database, throw "AnswerNotFoundException"
     * with the message code - 'ANS-001' and message - 'Entered answer uuid does not exist'.
     *
     * Else, delete the answer from the database and return "uuid" of the deleted answer and message "ANSWER DELETED"
     * in the JSON response with the corresponding HTTP status.
     *
     * @param authorization
     * @param ansUuid
     * @return AnswerDeleteResponse
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @RequestMapping(path = "/answer/delete/{answerId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("answerId") final String ansUuid) throws AuthorizationFailedException, AnswerNotFoundException {

        // business service call to delete the answer uuid passed from the front end.
        AnswerEntity ansEntity = ansService.deleteAnswer(ansUuid, authorization);

        // build the answer delete response
        AnswerDeleteResponse answerRsp = new AnswerDeleteResponse().id(ansEntity.getUuid()).status("ANSWER DELETED");

        return new ResponseEntity<>(answerRsp, HttpStatus.OK);

    }

}