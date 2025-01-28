package com.demo.newsApp.controller;

import com.demo.newsApp.config.AppConfig;
import com.demo.newsApp.exception.InvalidKeywordException;
import com.demo.newsApp.model.Article;
import com.demo.newsApp.model.ErrorResponse;
import com.demo.newsApp.model.Unit;
import com.demo.newsApp.services.NewsService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewsControllerTests {

    @Mock
    private NewsService newsService;

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private NewsController newsController;

    @Value("${offline.mode:false}")
    private boolean offlineMode;

    @Mock
    private HttpServletRequest request;


    // Test for toggleMode - Set to Online
    @Test
    public void toggleMode_shouldSetModeToOnline() {
        ResponseEntity<String> response = (ResponseEntity<String>) newsController.toggleMode("online", request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Mode successfully set to: online", response.getBody());
        verify(appConfig).setMode("online");
    }

    // Test for toggleMode - Set to Offline
    @Test
    public void toggleMode_shouldSetModeToOffline() {
        ResponseEntity<String> response = (ResponseEntity<String>) newsController.toggleMode("offline", request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Mode successfully set to: offline", response.getBody());
        verify(appConfig).setMode("offline");
    }

    // Test for toggleMode - Invalid Mode
    @Test
    public void toggleMode_shouldReturnBadRequestForInvalidMode() {
        ResponseEntity<ErrorResponse> response = (ResponseEntity<ErrorResponse>) newsController.toggleMode("invalid", request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid mode. Use 'online' or 'offline'.", response.getBody().getMessage());
        verify(appConfig, never()).setMode(anyString());
    }

    // Test for getGroupedNews - Valid Input
    @Test
    public void getGroupedNews_shouldReturnGroupedArticles() {
        String keyword = "validKeyword";
        int interval = 12;
        Unit unit = Unit.HOURS;
        Map<String, Object> groupedArticles = new HashMap<>();
        groupedArticles.put("12 hours ago", List.of(new Article()));

        when(newsService.getGroupedNews(keyword, interval, unit.getValue())).thenReturn(groupedArticles);

        ResponseEntity<EntityModel<Map<String, Object>>> response = (ResponseEntity<EntityModel<Map<String, Object>>>) newsController.getGroupedNews(keyword, interval, unit,request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(groupedArticles, response.getBody().getContent());
        verify(newsService).getGroupedNews(keyword, interval, unit.getValue());
    }

    // Test for getGroupedNews - Invalid Keyword Exception
    @Test
    public void getGroupedNews_shouldHandleInvalidKeywordException() {
        String keyword = "invalid!";
        int interval = 12;
        Unit unit = Unit.HOURS;

        when(newsService.getGroupedNews(keyword, interval, unit.getValue()))
                .thenThrow(new InvalidKeywordException("Invalid keyword"));

        ResponseEntity<ErrorResponse> response = (ResponseEntity<ErrorResponse>) newsController.getGroupedNews(keyword, interval, unit,request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid Keyword", response.getBody().getError());
        verify(newsService).getGroupedNews(keyword, interval, unit.getValue());
    }

    // Test for getGroupedNews - Unexpected Exception
    @Test
    public void getGroupedNews_shouldHandleUnexpectedException() {
        String keyword = "validKeyword";
        int interval = 12;
        Unit unit = Unit.HOURS;

        when(newsService.getGroupedNews(keyword, interval, unit.getValue()))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<ErrorResponse> response = (ResponseEntity<ErrorResponse>) newsController.getGroupedNews(keyword, interval, unit,request);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Internal Server Error", response.getBody().getError());
        verify(newsService).getGroupedNews(keyword, interval, unit.getValue());
    }
}
