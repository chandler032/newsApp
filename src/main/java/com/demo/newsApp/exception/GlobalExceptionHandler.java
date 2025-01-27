package com.demo.newsApp.exception;

import com.demo.newsApp.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Log4j2
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidKeywordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidKeywordException(InvalidKeywordException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid Keyword",
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }
    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<ErrorResponse> handleNoContentException(NoContentException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.NO_CONTENT.value(),
                        "No Content",
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericExceptions(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        "An unexpected error occurred.",
                        request.getRequestURI()
                )
        );
    }
}