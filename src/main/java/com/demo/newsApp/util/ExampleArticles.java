package com.demo.newsApp.util;

import com.demo.newsApp.model.Article;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ExampleArticles {
    public static final List<Article> EXAMPLE_ARTICLES = List.of(
            new Article("Apple releases new iPhone", "Latest technology from Apple.", "https://example.com/apple1", "2025-01-24T10:00:00Z"),
            new Article("Apple's stock hits record high", "Investors are thrilled.", "https://example.com/apple2", "2025-01-23T15:00:00Z"),
            new Article("New Apple store opening soon", "Exciting news for fans.", "https://example.com/apple3", "2025-01-22T08:00:00Z")
    );

    public static String getGroupKey(LocalDateTime dateTime, int interval, String unit) {
        switch (unit.toLowerCase()) {
            case "minutes":
                return dateTime.truncatedTo(ChronoUnit.MINUTES).withMinute((dateTime.getMinute() / interval) * interval).withSecond(0).toString();
            case "hours":
                return dateTime.truncatedTo(ChronoUnit.HOURS).withHour((dateTime.getHour() / interval) * interval).withMinute(0).withSecond(0).toString();
            case "days":
                return dateTime.truncatedTo(ChronoUnit.DAYS).toLocalDate().toString();
            case "weeks":
                LocalDateTime startOfWeek = dateTime.minusDays(dateTime.getDayOfWeek().getValue() - 1);
                return startOfWeek.truncatedTo(ChronoUnit.DAYS).toLocalDate().toString();
            case "months":
                return dateTime.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS).toLocalDate().toString();
            case "years":
                return dateTime.withDayOfYear(1).truncatedTo(ChronoUnit.DAYS).toLocalDate().toString();
            default:
                throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
    }

    public static LocalDateTime calculateStartTime(LocalDateTime endTime, int interval, String unit) {
        switch (unit.toLowerCase()) {
            case "hours":
                return endTime.minusHours(interval);
            case "days":
                return endTime.minusDays(interval);
            case "weeks":
                return endTime.minusWeeks(interval);
            case "months":
                return endTime.minusMonths(interval);
            case "years":
                return endTime.minusYears(interval);
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + unit);
        }
    }
}
