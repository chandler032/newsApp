package com.demo.newsApp.controller;


import com.demo.newsApp.services.impl.CacheServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheServiceImpl cacheService;

    @Autowired
    public CacheController(CacheServiceImpl cacheService) {
        this.cacheService = cacheService;
    }

    @Operation(summary = "Clear cache  by keyword", description = "clear cache based on the keyword.")
    @ApiResponse(responseCode = "201", description = "Successfully cleared the cache")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @PostMapping("/clear")
    public ResponseEntity<String> clearCache(@RequestParam("keyword") String keyword) {
        try {
            cacheService.evictCache(keyword);
            return ResponseEntity.ok("Cache cleared for keyword: " + keyword);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error clearing cache for keyword: " + keyword + " - " + e.getMessage());
        }
    }
}
