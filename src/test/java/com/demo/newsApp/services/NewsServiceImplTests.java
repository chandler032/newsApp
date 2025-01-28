package com.demo.newsApp.services;

import com.demo.newsApp.config.AesConfig;
import com.demo.newsApp.config.AppConfig;
import com.demo.newsApp.exception.InvalidKeywordException;
import com.demo.newsApp.exception.NoContentException;
import com.demo.newsApp.model.Article;
import com.demo.newsApp.model.NewsResponse;
import com.demo.newsApp.services.impl.NewsServiceImpl;
import com.demo.newsApp.util.ExampleArticles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewsServiceImplTests {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AesConfig aesConfig;

    @Mock
    private AppConfig appConfig;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private NewsServiceImpl newsService;

    private final String newsApiUrl = "http://example.com/news?apiKey={apiKey}&keyword={keyword}";

    @BeforeEach
    public void setUp() {
        newsService = new NewsServiceImpl(newsApiUrl, aesConfig, appConfig, restTemplate, cacheManager);
    }

    @Test
    public void getNews_shouldReturnNewsResponseForValidKeyword() throws Exception {
        // Arrange
        String keyword = "apple";
        String url = "http://example.com/news?apiKey=1234&keyword=apple";
        NewsResponse mockResponse = new NewsResponse("ok", 3, ExampleArticles.EXAMPLE_ARTICLES);

        when(appConfig.getMode()).thenReturn("online");
        when(aesConfig.getDecryptedApiKey()).thenReturn("1234");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                isNull(),
                eq(new ParameterizedTypeReference<NewsResponse>() {})
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // Act
        NewsResponse response = newsService.getNews(keyword);

        // Assert
        assertNotNull(response);
        assertEquals("ok", response.getStatus());
        assertEquals(3, response.getTotalResults());
    }

    @Test
    public void getNews_shouldThrowInvalidKeywordExceptionForInvalidKeyword() {
        // Arrange
        String keyword = "invalid!";

        // Act & Assert
        InvalidKeywordException exception = assertThrows(InvalidKeywordException.class, () -> newsService.getNews(keyword));
        assertEquals("Keyword must only contain letters and numbers and cannot be null.", exception.getMessage());
    }

    @Test
    public void getNews_shouldReturnMockDataInOfflineMode() {
        // Arrange
        String keyword = "apple";
        when(appConfig.getMode()).thenReturn("offline");

        // Act
        NewsResponse response = newsService.getNews(keyword);

        // Assert
        assertNotNull(response);
        assertEquals("ok", response.getStatus());
        assertFalse(response.getArticles().isEmpty());
    }

    @Test
    public void getNews_shouldFallbackToCacheWhenApiFails() throws Exception {
        // Arrange
        String keyword = "apple";
        List<Article> cachedArticles = ExampleArticles.EXAMPLE_ARTICLES;
        CaffeineCache mockCache = mock(CaffeineCache.class);
        when(cacheManager.getCache("articles")).thenReturn(mockCache);
        when(mockCache.get(keyword)).thenReturn(() -> cachedArticles);
        when(appConfig.getMode()).thenReturn("online");
        when(aesConfig.getDecryptedApiKey()).thenReturn("1234");

        doThrow(new RuntimeException("API Error")).when(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(new ParameterizedTypeReference<NewsResponse>() {})
        );

        // Act
        NewsResponse response = newsService.getNews(keyword);

        // Assert
        assertNotNull(response);
        assertEquals("ok", response.getStatus());
        assertEquals(cachedArticles.size(), response.getTotalResults());
    }

    @Test
    public void getNews_shouldThrowNoContentExceptionWhenNoArticlesAvailable() throws Exception {
        // Arrange
        String keyword = "apple";
        when(appConfig.getMode()).thenReturn("online");
        when(aesConfig.getDecryptedApiKey()).thenReturn("1234");
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(new ParameterizedTypeReference<NewsResponse>() {})
        )).thenReturn(new ResponseEntity<>(new NewsResponse("ok", 0, Collections.emptyList()), HttpStatus.OK));

        // Act & Assert
        NoContentException exception = assertThrows(NoContentException.class, () -> newsService.getNews(keyword));
        assertEquals("No news articles found for the given keyword.", exception.getMessage());
    }

    @Test
    public void groupArticlesByInterval_shouldGroupArticles() {
        // Arrange
        List<Article> articles = ExampleArticles.EXAMPLE_ARTICLES;
        int interval = 12;
        String unit = "months";

        // Act
        Map<String, Object> groupedArticles = newsService.groupArticlesByInterval(articles, interval, unit);

        // Assert
        assertNotNull(groupedArticles);
        assertFalse(groupedArticles.isEmpty());
    }

    @Test
    public void groupArticlesByInterval_shouldThrowNoContentExceptionWhenNoArticles() {
        // Arrange
        List<Article> articles = Collections.emptyList();
        int interval = 12;
        String unit = "hours";

        // Act & Assert
        NoContentException exception = assertThrows(NoContentException.class, () -> newsService.groupArticlesByInterval(articles, interval, unit));
        assertEquals("No news articles found for the given keyword.", exception.getMessage());
    }

    @Test
    public void filterArticlesByKeyword_shouldFilterArticlesCorrectly() {
        // Arrange
        String keyword = "example";
        List<Article> articles = List.of(
                new Article("Example Title 1", "Example Description 1", "http://example.com/1", "2024-01-01T00:00:00Z"),
                new Article("Another Title", "Another Description", "http://example.com/2", "2024-01-01T12:00:00Z")
        );

        // Act
        NewsResponse response = newsService.filterArticlesByKeyword(keyword, articles);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalResults());
        assertEquals("Example Title 1", response.getArticles().get(0).getTitle());
    }

    @Test
    public void getGroupedNews_shouldReturnGroupedArticles() throws Exception {
        // Arrange
        String keyword = "apple";
        int interval = 12;
        String unit = "months";
        NewsResponse mockResponse = new NewsResponse("ok", 3, ExampleArticles.EXAMPLE_ARTICLES);

        when(appConfig.getMode()).thenReturn("online");
        when(aesConfig.getDecryptedApiKey()).thenReturn("1234");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(new ParameterizedTypeReference<NewsResponse>() {})))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // Act
        Map<String, Object> response = newsService.getGroupedNews(keyword, interval, unit);

        // Assert
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}
