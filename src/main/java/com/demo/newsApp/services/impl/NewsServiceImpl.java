package com.demo.newsApp.services.impl;

import com.demo.newsApp.exception.InvalidKeywordException;
import com.demo.newsApp.exception.NoContentException;
import com.demo.newsApp.model.Article;
import com.demo.newsApp.model.NewsResponse;
import com.demo.newsApp.config.AesConfig;
import com.demo.newsApp.config.AppConfig;
import com.demo.newsApp.services.NewsService;
import com.demo.newsApp.util.ExampleArticles;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableCaching
@Log4j2
public class NewsServiceImpl implements NewsService {

    private final String newsApiUrl;
    private final AesConfig aesConfig;
    private final AppConfig appConfig;
    private final RestTemplate restTemplate;
    private final CacheManager cacheManager;

    public NewsServiceImpl(@Value("${news.api.url}") String newsApiUrl, AesConfig aesConfig, AppConfig appConfig, RestTemplate restTemplate, CacheManager cacheManager) {
        this.newsApiUrl = newsApiUrl;
        this.aesConfig = aesConfig;
        this.appConfig = appConfig;
        this.restTemplate = restTemplate;
        this.cacheManager = cacheManager;
    }

    @Override
    @Cacheable(value = "articles", key = "#keyword", unless = "#result == null")
    public NewsResponse getNews(String keyword) {

        validateKeyword(keyword);

        if (isOfflineMode()) {
            log.info("Application is in offline mode. Fetching example articles.");
            return filterArticlesByKeyword(keyword, ExampleArticles.EXAMPLE_ARTICLES);
        }

        try {
            log.info("Fetching news articles for keyword: {}", keyword);
            String url = buildApiUrl(keyword);

            ResponseEntity<NewsResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                List<Article> articles = response.getBody().getArticles();
                checkContent(articles);

                // Cache the articles for fallback
                cacheArticles(keyword, articles);
                return filterArticlesByKeyword(keyword, articles);
            }
            throw new NoContentException("No news articles found for the given keyword.");
        }catch(NoContentException ex){
            throw  ex;
        }
        catch (Exception ex) {
            log.error("Error fetching news articles for keyword: {}. Falling back to cache.", keyword, ex);

            // Fallback to cache
            List<Article> cachedArticles = getCachedArticles(keyword);
            if (!CollectionUtils.isEmpty(cachedArticles)) {
                log.info("Returning cached articles for keyword: {}", keyword);
                return filterArticlesByKeyword(keyword, cachedArticles);
            }
            // If cache is also empty, throw an error
            throw new NoContentException("No news articles available for the given keyword.");
        }
    }

    private boolean isOfflineMode() {
        return "offline".equalsIgnoreCase(appConfig.getMode());
    }

    private String buildApiUrl(String keyword) throws Exception {
        return newsApiUrl.replace("{apiKey}", aesConfig.getDecryptedApiKey())
                .replace("{keyword}", keyword);
    }

    private void validateKeyword(String keyword) {
        if (keyword == null || !keyword.matches("^[a-zA-Z0-9]+$")) {
            throw new InvalidKeywordException("Keyword must only contain letters and numbers and cannot be null.");
        }
    }

    private void checkContent(List<Article> articles) {
        if (CollectionUtils.isEmpty(articles)) {
            throw new NoContentException("No news articles found for the given keyword.");
        }
    }

    private List<Article> getCachedArticles(String keyword) {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache("articles");
        if (null != cache  && null != cache.get(keyword)) {
            List<Article> articles =(List<Article>) cache.get(keyword).get();

            return null !=articles && !CollectionUtils.isEmpty(articles) ? articles :Collections.emptyList();
        }
        return Collections.emptyList();
    }

    private void cacheArticles(String keyword, List<Article> articles) {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache("articles");
        if (null != cache ) {
            cache.put(keyword, articles);
        }
    }

    @Override
    public Map<String, Object> getGroupedNews(String keyword, int interval, String unit) {
        NewsResponse newsResponse = getNews(keyword);
        List<Article> filteredArticles = newsResponse.getArticles();
        return groupArticlesByInterval(filteredArticles, interval, unit);
    }

    public NewsResponse filterArticlesByKeyword(String keyword, List<Article> articles) {
        List<Article> filteredArticles = articles.stream()
                .filter(article -> article.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        article.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        checkContent(filteredArticles);
        return new NewsResponse("ok", filteredArticles.size(), filteredArticles);
    }

    public Map<String, Object> groupArticlesByInterval(List<Article> articles, int interval, String unit) {
        Map<String, List<Article>> groupedArticles = new HashMap<>();

        for (Article article : articles) {
            Instant publishedAtInstant = Instant.parse(article.getPublishedAt());
            LocalDateTime publishedAt = publishedAtInstant.atZone(ZoneId.systemDefault()).toLocalDateTime();
            String groupKey = ExampleArticles.getGroupKey(publishedAt, interval, unit);
            groupedArticles.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(article);
        }

        Map<String, Object> response = new HashMap<>();
        groupedArticles.forEach((key, value) -> response.put(key, Map.of("count", value.size(), "articles", value)));
        return response;
    }
}