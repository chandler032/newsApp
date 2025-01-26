package com.demo.newsApp.controller;

import com.demo.newsApp.config.AppConfig;
import com.demo.newsApp.model.NewsResponse;
import com.demo.newsApp.model.Unit;
import com.demo.newsApp.services.NewsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.EntityModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api/news")
public class NewsController {
    private final NewsService newsService;
    private final AppConfig appConfig;

    public NewsController(NewsService newsService, AppConfig appConfig) {
        this.newsService = newsService;
        this.appConfig = appConfig;
    }

    @Operation(summary = "Search news articles", description = "Searches news articles based on the keyword and optional date interval.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved news articles")
    @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    @GetMapping("/search")
    public ResponseEntity<NewsResponse> searchNews(@RequestParam String keyword) {
        try {
            log.info("Request started news search");
            return ResponseEntity.ok(newsService.getNews(keyword));
        }
        catch (Exception ex){
            log.atError().withThrowable(ex).
                    log("Exception occurred while fetching news, exception :{}",
                    ex.getMessage());
            throw ex;
        }
    }

    @Operation(summary = "Group news articles", description = "Groups news articles by publication date into intervals (e.g., minutes, hours, days, weeks, months, years) for easy analysis and categorization.")
    @ApiResponse(responseCode = "200", description = "Successfully grouped news articles")
    @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    @GetMapping(value="/newsGroupByInterval",produces = MediaTypes.ALPS_JSON_VALUE)
    public ResponseEntity<EntityModel<Map<String, Object>>> getGroupedNews(@RequestParam String keyword,
                                              @RequestParam(defaultValue = "12") int interval,
                                              @RequestParam(defaultValue = "hours") Unit unit) {
        try {
            log.info("Request started for news search with unit {} and interval {}", unit,interval);
            Map<String,Object> groupedArticles = newsService.getGroupedNews(keyword, interval, unit.getValue());
            EntityModel<Map<String,Object>> resource = EntityModel.of(groupedArticles);
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NewsController.class)
                    .getGroupedNews(keyword, interval, unit)).withSelfRel());
            return ResponseEntity.ok(resource);
        }
        catch (Exception ex){
            log.atError().withThrowable(ex).
                    log("Exception occurred while fetching news, exception :{}",
                    ex.getMessage());
            throw ex;
        }
    }

    @ApiResponse(responseCode = "201", description = "Successfully Set the mode")
    @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    @PostMapping("/toggle-mode")
    public ResponseEntity<String> toggleMode(@RequestParam String mode) {
        if ("online".equalsIgnoreCase(mode) || "offline".equalsIgnoreCase(mode)) {
            appConfig.setMode(mode);
            return ResponseEntity.ok("Mode set to: " + mode);
        }
        return ResponseEntity.badRequest().body("Invalid mode. Use 'online' or 'offline'.");
    }
}
