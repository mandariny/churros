package com.a503.churros.global.exception;

import io.jsonwebtoken.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;


@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({ HttpMessageNotReadableException.class })
    public ResponseEntity<ErrorResponse> HttpException(HttpMessageNotReadableException ex) {
        System.out.println("핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리");
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST .value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(HttpClientErrorException.Forbidden ex) {
        System.out.println("핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리");
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
        System.out.println("핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리");
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ RuntimeException.class , IOException.class})
    public ResponseEntity<ErrorResponse> RuntimeException(Exception ex) {
        System.out.println("핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리");
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        System.out.println("핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리");
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        System.out.println("핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리");
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ Exception.class})
    public ResponseEntity<ErrorResponse> Exception(Exception ex) {
        System.out.println("핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리핸들러 예외처리");
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}