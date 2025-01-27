package com.demo.newsApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsResponse {
    private String status;
    private int totalResults;
    private List<Article> articles;
}

