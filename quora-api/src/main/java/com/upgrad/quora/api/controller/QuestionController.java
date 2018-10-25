package com.upgrad.quora.api.controller;


import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserAdminService;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.QuestionNotFoundException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.type.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionEditResponse;


import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller class for defining question related operations.
 */
@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    UserAdminService userAdminService;

    @Autowired
    QuestionService questionService;

    /**
     * Rest Endpoint method implementation used for creating question for authorized user.
     * Only logged-in user is allowed to create a question.
     *
     * @param questionRequest request object of question instance
     * @param authorization   access token of user
     * @return ResponseEntity object with response details of question
     * @throws AuthorizationFailedException if user is not signed then this exception is thrown
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader final String authorization) throws AuthorizationFailedException {
        UserAuthEntity authorizedUser = userAdminService.getUserByAccessToken(authorization, ActionType.CREATE);
        UserEntity user = authorizedUser.getUser();
        Question question = new Question();
        question.setUser(authorizedUser.getUser());
        question.setUuid(UUID.randomUUID().toString());
        question.setContent(questionRequest.getContent());
        final ZonedDateTime now = ZonedDateTime.now();
        question.setDate(now);
        Question createdQuestion = questionService.createQuestion(question);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    /**
     * Rest Endpoint method implementation used for getting all questions for authorized user.
     * Only logged in user is allowed to get the details.
     *
     * @param authorization authorized user
     * @return ResponseEntity object with response details of question
     * @throws AuthorizationFailedException if user is not signed then this exception is thrown
     * @throws QuestionNotFoundException    thrown if there are o questions for the user
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestions(@RequestHeader final String authorization) throws AuthorizationFailedException, QuestionNotFoundException {
        UserAuthEntity authorizedUser = userAdminService.getUserByAccessToken(authorization, ActionType.ALL_QUESTION);
        //Since the user is authorized, go for extracting questions for all users
        List<Question> questionList = questionService.getAllQuestions();
        StringBuilder builder = new StringBuilder();
        getContentsString(questionList, builder);
        StringBuilder uuIdBuilder = new StringBuilder();
        getUuIdString(questionList, uuIdBuilder);
        QuestionDetailsResponse questionResponse = new QuestionDetailsResponse()
                .id(uuIdBuilder.toString())
                .content(builder.toString());
        return new ResponseEntity<QuestionDetailsResponse>(questionResponse, HttpStatus.OK);
    }


    /**
     * Rest Endpoint method implementation used for getting all questions for any user.
     * Only logged-in user and the owner of the question is allowed to use this endpoint.
     *
     * @param questionEditRequest request for question to be edited
     * @param questionId          question to be edited
     * @param authorization       Authorized user
     * @return Response Entity of type QuestionEditResponse
     * @throws AuthorizationFailedException if user is not signed then this exception is thrown
     * @throws InvalidQuestionException     if question does not exist
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity authorizedUser = userAdminService.getUserByAccessToken(authorization, ActionType.EDIT);
        //Check if the user himself is the owner and trying to edit it and return the question object
        Question question = questionService.isUserQuestionOwner(questionId, authorizedUser, ActionType.EDIT);
        question.setContent(questionEditRequest.getContent());
        questionService.editQuestion(question);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(question.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }


    /**
     * Rest Endpoint method implementation used for deleting question by question id.
     * Only logged-in user who is owner of the question or admin is allowed to delete a question
     *
     * @param questionUuId  questionid to be deleted
     * @param authorization user to be authorized
     * @return ResponseEnitty object of type QuestionDeleteResponse
     * @throws AuthorizationFailedException if user is not signed then this exception is thrown
     * @throws InvalidQuestionException     if question does not exist
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> userDelete(@PathVariable("questionId") final String questionUuId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity authorizedUser = userAdminService.getUserByAccessToken(authorization, ActionType.DELETE);
        //Check if the user himself is the owner and trying to edit it and return the question object
        Question question = questionService.isUserQuestionOwner(questionUuId, authorizedUser, ActionType.DELETE);
        questionService.deleteQuestion(question);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse()
                .id(question.getUuid())
                .status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    //getAllQuestionsByUser

    @RequestMapping(method = RequestMethod.GET, path = "/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestionsByUser(@PathVariable("userId") final String uuId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, QuestionNotFoundException, UserNotFoundException {
        UserAuthEntity authorizedUser = userAdminService.getUserByAccessToken(authorization, ActionType.ALL_FOR_USER);
        //Get the list of questions for the user
        List<Question> questionList = questionService.getQuestionsForUser(uuId);
        StringBuilder contentBuilder = new StringBuilder();
        StringBuilder uuIdBuilder = new StringBuilder();
        getContentsString(questionList, contentBuilder);
        getUuIdString(questionList, uuIdBuilder);
        QuestionDetailsResponse questionResponse = new QuestionDetailsResponse()
                .id(uuIdBuilder.toString())
                .content(contentBuilder.toString());
        return new ResponseEntity<QuestionDetailsResponse>(questionResponse, HttpStatus.OK);
    }

    /**
     * private utility method for appending the uuid of questions.
     *
     * @param questionList List of questions
     * @param uuIdBuilder  StringBuilder object
     */
    private StringBuilder getUuIdString(List<Question> questionList, StringBuilder uuIdBuilder) {
        for (Question questionObject : questionList) {
            uuIdBuilder.append(questionObject.getUuid()).append(",");
        }
        return uuIdBuilder;
    }

    /**
     * private utility method for providing contents string in appended format
     *
     * @param questionList list of questions
     * @param builder      StringBuilder with appended content list.
     */
    private StringBuilder getContentsString(List<Question> questionList, StringBuilder builder) {
        for (Question questionObject : questionList) {
            builder.append(questionObject.getContent()).append(",");
        }
        return builder;
    }

}
