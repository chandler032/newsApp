package com.demo.newsApp.controller;

import com.demo.newsApp.config.AppConfig;
import com.demo.newsApp.exception.InvalidKeywordException;
import com.demo.newsApp.exception.NoContentException;
import com.demo.newsApp.model.ErrorResponse;
import com.demo.newsApp.model.NewsResponse;
import com.demo.newsApp.model.Unit;
import com.demo.newsApp.services.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@RestController
@Validated
@Log4j2
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    private final AppConfig appConfig;

    public NewsController(NewsService newsService, AppConfig appConfig) {
        this.newsService = newsService;
        this.appConfig = appConfig;
    }
    @Operation(
            summary = "Group news articles",
            description = "Groups news articles by publication date into specific intervals such as minutes, hours, days, weeks, etc."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully grouped news articles", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NewsResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @GetMapping(value = "/newsGroupNewsByInterval")
    public ResponseEntity<?> getGroupedNews(@RequestParam String keyword,
                                            @RequestParam(defaultValue = "12") @Min(value = 1, message = "Value must be positive")
                                            int interval,
                                            @RequestParam(defaultValue = "hours") Unit unit,
                                            HttpServletRequest request) {
        try {
            log.info("Grouping news articles for keyword: {}, interval: {}, unit: {}", keyword, interval, unit);
            Map<String, Object> groupedArticles = newsService.getGroupedNews(keyword, interval, unit.getValue());

            EntityModel<Map<String, Object>> resource = EntityModel.of(groupedArticles);
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NewsController.class)
                    .getGroupedNews(keyword, interval, unit, request)).withSelfRel());

            return ResponseEntity.ok(resource);
        }catch (NoContentException exception) {
            log.error("No content found for keyword: {}", keyword, exception);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (InvalidKeywordException exception) {
            log.error("Invalid keyword provided for grouping: {}", keyword, exception);
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
            log.error("Unexpected error while grouping news articles: {}", keyword, ex);
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

    @Operation(
            summary = "Set application mode",
            description = "Sets the application mode to either 'online' or 'offline'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the application mode", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @PostMapping("/toggle-mode")
    public ResponseEntity<?> toggleMode(@RequestParam String mode, HttpServletRequest request) {
        try {
            if ("online".equalsIgnoreCase(mode) || "offline".equalsIgnoreCase(mode)) {
                appConfig.setMode(mode);
                return ResponseEntity.ok("Mode successfully set to: " + mode);
            }
            throw new IllegalArgumentException("Invalid mode. Use 'online' or 'offline'.");
        } catch (IllegalArgumentException ex) {
            log.error("Invalid mode provided: {}", mode, ex);
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