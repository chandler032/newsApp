package com.demo.newsApp.exception;

import com.demo.newsApp.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Log4j2
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidKeywordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidKeywordException(InvalidKeywordException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError("Bad Request");
        errorResponse.setMessage(ex.getMessage()); // Custom error message
        errorResponse.setPath(request.getDescription(false).replace("uri=", "")); // Dynamically set the path
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<ErrorResponse> handleNoContentException(NoContentException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.NO_CONTENT.value());
        errorResponse.setError("No content");
        errorResponse.setMessage(ex.getMessage()); // Custom error message
        errorResponse.setPath(request.getDescription(false).replace("uri=", "")); // Dynamically set the path
        return new ResponseEntity<>(errorResponse, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericExceptions(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setError("Internal Server Error");
        errorResponse.setMessage("An unexpected error occurred: " + ex.getMessage());
        errorResponse.setPath(request.getDescription(false).replace("uri=", "")); // Dynamically set the path
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}