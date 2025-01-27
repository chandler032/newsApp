package com.demo.newsApp.util;

import com.demo.newsApp.model.Article;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExampleArticlesTests {

    @Test
    public void exampleArticles_shouldContainExpectedArticles() {
        // Arrange
        List<Article> expectedArticles = List.of(
                new Article("Apple releases new iPhone", "Latest technology from Apple.", "https://example.com/apple1", "2025-01-24T10:00:00Z"),
                new Article("Apple's stock hits record high", "Investors are thrilled.", "https://example.com/apple2", "2025-01-23T15:00:00Z"),
                new Article("New Apple store opening soon", "Exciting news for fans.", "https://example.com/apple3", "2025-01-22T08:00:00Z")
        );

        // Act
        List<Article> actualArticles = ExampleArticles.EXAMPLE_ARTICLES;

        // Assert
        assertEquals(expectedArticles.size(), actualArticles.size());
        for (int i = 0; i < expectedArticles.size(); i++) {
            assertEquals(expectedArticles.get(i).getTitle(), actualArticles.get(i).getTitle());
            assertEquals(expectedArticles.get(i).getDescription(), actualArticles.get(i).getDescription());
            assertEquals(expectedArticles.get(i).getUrl(), actualArticles.get(i).getUrl());
            assertEquals(expectedArticles.get(i).getPublishedAt(), actualArticles.get(i).getPublishedAt());
        }
    }

    @Test
    public void getGroupKey_shouldReturnCorrectKeyForMinutes() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.parse("2025-01-24T10:15:30");
        int interval = 15;
        String unit = "minutes";

        // Act
        String groupKey = ExampleArticles.getGroupKey(dateTime, interval, unit);

        // Assert
        assertEquals("2025-01-24T10:15", groupKey);
    }

    @Test
    public void getGroupKey_shouldReturnCorrectKeyForHours() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.parse("2025-01-24T10:15:30");
        int interval = 1;
        String unit = "hours";

        // Act
        String groupKey = ExampleArticles.getGroupKey(dateTime, interval, unit);

        // Assert
        assertEquals("2025-01-24T10:00", groupKey);
    }

    @Test
    public void getGroupKey_shouldReturnCorrectKeyForDays() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.parse("2025-01-24T10:15:30");
        int interval = 1;
        String unit = "days";

        // Act
        String groupKey = ExampleArticles.getGroupKey(dateTime, interval, unit);

        // Assert
        assertEquals("2025-01-24", groupKey);
    }

    @Test
    public void getGroupKey_shouldReturnCorrectKeyForWeeks() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.parse("2025-01-24T10:15:30");
        int interval = 1;
        String unit = "weeks";

        // Act
        String groupKey = ExampleArticles.getGroupKey(dateTime, interval, unit);

        // Assert
        assertEquals("2025-01-20", groupKey); // Week starts on Monday (2025-01-20)
    }

    @Test
    public void getGroupKey_shouldReturnCorrectKeyForMonths() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.parse("2025-01-24T10:15:30");
        int interval = 1;
        String unit = "months";

        // Act
        String groupKey = ExampleArticles.getGroupKey(dateTime, interval, unit);

        // Assert
        assertEquals("2025-01-01", groupKey);
    }

    @Test
    public void getGroupKey_shouldReturnCorrectKeyForYears() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.parse("2025-01-24T10:15:30");
        int interval = 1;
        String unit = "years";

        // Act
        String groupKey = ExampleArticles.getGroupKey(dateTime, interval, unit);

        // Assert
        assertEquals("2025-01-01", groupKey);
    }

    @Test
    public void getGroupKey_shouldThrowExceptionForUnsupportedUnit() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.parse("2025-01-24T10:15:30");
        int interval = 1;
        String unit = "invalidUnit";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ExampleArticles.getGroupKey(dateTime, interval, unit);
        });

        assertEquals("Unsupported unit: invalidUnit", exception.getMessage());
    }
}