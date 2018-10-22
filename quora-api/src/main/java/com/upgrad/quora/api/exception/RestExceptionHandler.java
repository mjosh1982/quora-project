package com.upgrad.quora.api.exception;


import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * An advice created to intercept the exceptions thrown by the application.
 * The respective methods of the exception intercept the exception and throw
 * relevant messages to the rest API.s
 */
@ControllerAdvice
public class RestExceptionHandler {


    /**
     * Handler method for @AnswerNotFoundException
     *
     * @param exe AnswerNotFoundException instance
     * @param req WebRequest instance
     * @return
     */
    @ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<ErrorResponse> answerNotFoundException(AnswerNotFoundException exe, WebRequest req) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * Handler method for @AuthenticationFailedException
     *
     * @param exe AuthenticationFailedException instance
     * @param req WebRequest instance
     * @return
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException exe, WebRequest req) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }

    /**
     * Handler method for @AuthorizationFailedException
     *
     * @param exe AuthorizationFailedException instance
     * @param req WebRequest instance
     * @return
     */
    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException exe, WebRequest req) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }

    /**
     * Handler method for @InvalidQuestionException
     *
     * @param exe InvalidQuestionException instance
     * @param req WebRequest instance
     * @return
     */
    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> invaildQuestionException(InvalidQuestionException exe, WebRequest req) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * Handler method for @SignOutRestrictedException
     *
     * @param exe SignOutRestrictedException instance
     * @param req WebRequest instance
     * @return
     */
    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> signOutRestrictedException(SignOutRestrictedException exe, WebRequest req) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * Handler method for @SignUpRestrictedException
     *
     * @param exe SignUpRestrictedException instance
     * @param req WebRequest instance
     * @return
     */
    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictedException(SignUpRestrictedException exe, WebRequest req) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * Handler method for @UserNotFoundException
     *
     * @param exe UserNotFoundException instance
     * @param req WebRequest instance
     * @return
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException exe, WebRequest req) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }
}
