package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.GET,path = "/question/all",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        List<QuestionEntity> questionEntities = questionBusinessService.getAllQuestions(authorization);

        List<QuestionDetailsResponse> questionDetailsResponseList = new LinkedList<>();//list is created to return.


        for(QuestionEntity questionEntity:questionEntities){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent());
            questionDetailsResponseList.add(questionDetailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList, HttpStatus.OK);
    }


   /*This endpoint is a GET request and is used to fetch all the questions that have been posted in the application
   it throws an AuthorizationFailedException if the access token provided by the user does not exist in the database*/

    @RequestMapping(method = RequestMethod.GET,path = "question/all/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable(value = "userId")final String uuid,@RequestHeader(value = "authorization")final String authorization) throws UserNotFoundException, AuthorizationFailedException {

        List<QuestionEntity> questionEntities = questionBusinessService.getAllQuestionsByUser(uuid,authorization);

        List<QuestionDetailsResponse> questionDetailsResponseList = new LinkedList<>();//list is created to return.


        for(QuestionEntity questionEntity:questionEntities){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent());
            questionDetailsResponseList.add(questionDetailsResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList, HttpStatus.OK);

    }


    /*This endpoint is a PUT request and is used to edit a question posted by the user, it throws an AuthorizationFailedException if the
    access token provided by the user does not exist in the database*/

    @RequestMapping(method = RequestMethod.POST, path="/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUuid(UUID.randomUUID().toString());
        QuestionEntity createdQuestion = questionBusinessService.createQuestion(questionEntity, authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");
        return  new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }
    /*This endpoint is a GET request and is used to fetch all the questions posed by a user it
    throws a UserNotFoundException if the user with UUID whose questions are to be retrieved from the database doesn't
    exist.*/

    @RequestMapping(method = RequestMethod.GET , path = "/question/all/{userId}" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization )
            throws AuthorizationFailedException , UserNotFoundException {

        final UserAuthEntity userAuthEntity = userAuthBusinessService.getUser(authorization);

        final List<QuestionEntity> allQuestionByUser = questionBusinessService.getAllQuestionsByUser(userId , userAuthEntity);

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionslist(allQuestionByUser), HttpStatus.OK);

    }

    /*This endpoint is a DELETE request and is used to delete a question that has been posted by a user.*/

    @RequestMapping(method = RequestMethod.DELETE , path = "/question/delete/{questionId}" ,  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String questionid )
            throws AuthorizationFailedException , InvalidQuestionException {

        final UserAuthEntity userAuthEntity = userAuthBusinessService.getUser(authorization);


        QuestionEntity deletedQuestion = questionBusinessService.deleteQuestion(questionid, userAuthEntity);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(deletedQuestion.getUuid()).status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);

    }
    }
}
