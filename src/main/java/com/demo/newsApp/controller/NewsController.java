package com.demo.newsApp.controller;

import com.demo.newsApp.config.AppConfig;
import com.demo.newsApp.exception.InvalidKeywordException;
import com.demo.newsApp.exception.NoContentException;
import com.demo.newsApp.model.ErrorResponse;
import com.demo.newsApp.model.NewsResponse;
import com.demo.newsApp.model.Unit;
import com.demo.newsApp.services.NewsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.EntityModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping("/search")
    public ResponseEntity<?> searchNews(@RequestParam String keyword, HttpServletRequest request) {
        try {
            log.info("Request started news search for keyword: {}", keyword);
            return ResponseEntity.ok(newsService.getNews(keyword));
        } catch (InvalidKeywordException exception) {
            log.error("InvalidKeywordException occurred for keyword: {}", keyword, exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Invalid Keyword",
                            exception.getMessage(),
                            request.getRequestURI()
                    )
            );
        } catch (NoContentException exception) {
            log.error("NoContentException occurred for keyword: {}", keyword, exception);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (Exception ex) {
            log.error("Unhandled exception occurred while fetching news for keyword: {}", keyword, ex);
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

    @Operation(summary = "Group news articles", description = "Groups news articles by publication date into intervals (e.g., minutes, hours, days, weeks, months, years) for easy analysis and categorization.")
    @ApiResponse(responseCode = "200", description = "Successfully grouped news articles")
    @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping(value = "/newsGroupByInterval", produces = MediaTypes.ALPS_JSON_VALUE)
    public ResponseEntity<?> getGroupedNews(@RequestParam String keyword,
                                            @RequestParam(defaultValue = "12") int interval,
                                            @RequestParam(defaultValue = "hours") Unit unit,
                                            HttpServletRequest request) {
        try {
            log.info("Request started for news grouping with unit: {} and interval: {}", unit, interval);

            // Call service to get grouped articles
            Map<String, Object> groupedArticles = newsService.getGroupedNews(keyword, interval, unit.getValue());

            // Build EntityModel with HATEOAS link
            EntityModel<Map<String, Object>> resource = EntityModel.of(groupedArticles);
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NewsController.class)
                    .getGroupedNews(keyword, interval, unit,request)).withSelfRel());

            return ResponseEntity.ok(resource);

        } catch (InvalidKeywordException exception) {
            log.error("InvalidKeywordException occurred for keyword: {}", keyword, exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Invalid Keyword",
                            exception.getMessage(),
                            request.getRequestURI()
                    )
            );
        } catch (Exception ex) {
            log.error("Unhandled exception occurred for keyword: {}", keyword, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Internal Server Error",
                            "An unexpected error occurred while grouping news articles.",
                            request.getRequestURI()
                    )
            );
        }
    }


    @Operation(summary = "Set application mode", description = "Sets the application mode to either 'online' or 'offline'.")
    @ApiResponse(responseCode = "200", description = "Successfully set the mode")
    @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    @PostMapping("/toggle-mode")
    public ResponseEntity<?> toggleMode(@RequestParam String mode, HttpServletRequest request) {
        try {
            // Validate mode input
            if ("online".equalsIgnoreCase(mode) || "offline".equalsIgnoreCase(mode)) {
                appConfig.setMode(mode);
                return ResponseEntity.ok("Mode successfully set to: " + mode);
            }

            // Handle invalid mode
            throw new IllegalArgumentException("Invalid mode. Use 'online' or 'offline'.");
        } catch (IllegalArgumentException ex) {
            log.error("Invalid mode provided: {}", mode, ex);

            // Return structured error response for invalid input
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Invalid Input",
                            ex.getMessage(),
                            request.getRequestURI()
                    )
            );
        } catch (Exception ex) {
            log.error("Unexpected error while setting mode: {}", mode, ex);

            // Return structured error response for unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Internal Server Error",
                            "An unexpected error occurred while setting the mode.",
                            request.getRequestURI()
                    )
            );
        }
    }
}
