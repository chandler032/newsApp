package com.demo.newsApp.controller;

import com.demo.newsApp.config.AppConfig;
import com.demo.newsApp.exception.InvalidKeywordException;
import com.demo.newsApp.model.Article;
import com.demo.newsApp.model.NewsResponse;
import com.demo.newsApp.model.Unit;
import com.demo.newsApp.services.NewsService;
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


    @Test
    public void searchNews_shouldReturnNewsResponse() {
        // Arrange
        String keyword = "validKeyword";
        NewsResponse expectedResponse = new NewsResponse("ok", 10, List.of(new Article()));
        when(newsService.getNews(keyword)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<NewsResponse> response = newsController.searchNews(keyword);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(newsService).getNews(keyword); // Verify the service was called
    }

    @Test
    public void searchNews_shouldThrowInvalidKeywordException() {
        // Arrange
        String keyword = "invalid!";
        when(newsService.getNews(keyword)).thenThrow(new InvalidKeywordException("Invalid keyword"));

        // Act & Assert
        InvalidKeywordException exception = assertThrows(InvalidKeywordException.class, () -> {
            newsController.searchNews(keyword);
        });

        assertEquals("Invalid keyword", exception.getMessage());
        verify(newsService).getNews(keyword); // Verify the service was called
    }


    @Test
    public void searchNews_shouldThrowUnexpectedException() {
        // Arrange
        String keyword = "validKeyword";
        when(newsService.getNews(keyword)).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            newsController.searchNews(keyword);
        });

        assertEquals("Unexpected error", exception.getMessage());
        verify(newsService).getNews(keyword); // Verify the service was called
    }

    @Test
    public void toggleMode_shouldSetModeToOnline() {
        // Act
        ResponseEntity<String> response = newsController.toggleMode("online");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Mode set to: online", response.getBody());
        verify(appConfig).setMode("online"); // Verify the mode was set
    }

    @Test
    public void toggleMode_shouldSetModeToOffline() {
        // Act
        ResponseEntity<String> response = newsController.toggleMode("offline");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Mode set to: offline", response.getBody());
        verify(appConfig).setMode("offline"); // Verify the mode was set
    }

    @Test
    public void toggleMode_shouldReturnBadRequestForInvalidMode() {
        // Act
        ResponseEntity<String> response = newsController.toggleMode("invalid");

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid mode. Use 'online' or 'offline'.", response.getBody());
        verify(appConfig, never()).setMode(anyString()); // Verify the mode was not set
    }


    @Test
    public void getGroupedNews_shouldReturnGroupedArticles() {
        // Arrange
        String keyword = "validKeyword";
        int interval = 12;
        Unit unit = Unit.HOURS;
        Map<String, Object> groupedArticles = new HashMap<>();
        groupedArticles.put("12 hours ago", List.of(new Article()));

        when(newsService.getGroupedNews(keyword, interval, unit.getValue())).thenReturn(groupedArticles);

        // Act
        ResponseEntity<EntityModel<Map<String, Object>>> response = newsController.getGroupedNews(keyword, interval, unit);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(groupedArticles, response.getBody().getContent());
        verify(newsService).getGroupedNews(keyword, interval, unit.getValue()); // Verify the service was called
    }

    @Test
    public void getGroupedNews_shouldThrowInvalidKeywordException() {
        // Arrange
        String keyword = "invalid!";
        int interval = 12;
        Unit unit = Unit.HOURS;

        when(newsService.getGroupedNews(keyword, interval, unit.getValue()))
                .thenThrow(new InvalidKeywordException("Invalid keyword"));

        // Act & Assert
        InvalidKeywordException exception = assertThrows(InvalidKeywordException.class, () -> {
            newsController.getGroupedNews(keyword, interval, unit);
        });

        assertEquals("Invalid keyword", exception.getMessage());
        verify(newsService).getGroupedNews(keyword, interval, unit.getValue()); // Verify the service was called
    }

    @Test
    public void getGroupedNews_shouldThrowUnexpectedException() {
        // Arrange
        String keyword = "validKeyword";
        int interval = 12;
        Unit unit = Unit.HOURS;

        when(newsService.getGroupedNews(keyword, interval, unit.getValue()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            newsController.getGroupedNews(keyword, interval, unit);
        });

        assertEquals("Unexpected error", exception.getMessage());
        verify(newsService).getGroupedNews(keyword, interval, unit.getValue()); // Verify the service was called
    }

}