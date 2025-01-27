package com.demo.newsApp.exception;

import com.demo.newsApp.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void testHandleInvalidKeywordException() {
        // Arrange
        InvalidKeywordException ex = new InvalidKeywordException("Invalid keyword provided");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/news");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidKeywordException(ex, httpServletRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid keyword provided", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("Invalid Keyword", response.getBody().getError());
        assertEquals("/api/news", response.getBody().getPath());
    }

    @Test
    public void testHandleNoContentException() {
        // Arrange
        NoContentException ex = new NoContentException("No content found");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/news");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNoContentException(ex, httpServletRequest);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("No content found", response.getBody().getMessage());
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getBody().getStatus());
        assertEquals("No Content", response.getBody().getError());
        assertEquals("/api/news", response.getBody().getPath());
    }

    @Test
    public void testHandleGenericExceptions() {
        // Arrange
        Exception ex = new Exception("Unexpected error occurred");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/news");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericExceptions(ex, httpServletRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred.", response.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("/api/news", response.getBody().getPath());
    }
}
