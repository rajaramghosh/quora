package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/question")
public class QuestionController
{

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @Autowired
    private UserDao userDao;

    //Method to create questions in the application and uses POST request method
    @RequestMapping(method = RequestMethod.POST, path = "create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization) {

       UserAuthEntity user = userDao.getUserAuthByToken(authorization);
        final QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUser_id(user.getUserId());

        final QuestionEntity createdQuestionEntity = questionBusinessService.create(questionEntity);

        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);

    }

    //Method to view all questions in the application and uses GET request method
    @RequestMapping(method = RequestMethod.GET, path = "all", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestions(final QuestionDetailsResponse questionDetailsResponse)  {
        return new ResponseEntity(HttpStatus.OK);
    }

    //Method to edit questions in the application and uses PUT request method
    @RequestMapping(method = RequestMethod.PUT, path = "edit", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditRequest> editQuestionContent(final QuestionEditRequest questionEditRequest) {
        return new ResponseEntity(HttpStatus.OK);

    }

    //Method to DELETE questions in the application and uses DELETE request method
    @RequestMapping(method = RequestMethod.DELETE, path = "delete", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(final QuestionDeleteResponse questionDeleteResponse)  {
        return new ResponseEntity(HttpStatus.OK);

    }

   /* //Method to view all questions in the application and uses GET request method
    @RequestMapping(method = RequestMethod.GET, path = "all", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestionsByUser(final QuestionDetailsResponse questionDetailsResponse)  {
        return new ResponseEntity(HttpStatus.OK);

    }
*/

}
