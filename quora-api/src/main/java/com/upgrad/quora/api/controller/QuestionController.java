package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
public class QuestionController
{

    //Method to create questions in the application and uses POST request method
    @RequestMapping(method = RequestMethod.POST, path = "create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionRequest> createQuestion(final QuestionRequest questionRequest) {
    }

    //Method to view all questions in the application and uses GET request method
    @RequestMapping(method = RequestMethod.GET, path = "all", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestions(final QuestionDetailsResponse questionDetailsResponse)  {
    }

    //Method to edit questions in the application and uses PUT request method
    @RequestMapping(method = RequestMethod.PUT, path = "edit", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditRequest> editQuestionContent(final QuestionEditRequest questionEditRequest) {
    }

    //Method to DELETE questions in the application and uses DELETE request method
    @RequestMapping(method = RequestMethod.DELETE, path = "delete", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(final QuestionDeleteResponse questionDeleteResponse)  {
    }

    //Method to view all questions in the application and uses GET request method
    @RequestMapping(method = RequestMethod.GET, path = "all", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestionsByUser(final QuestionDetailsResponse questionDetailsResponse)  {
    }


}
