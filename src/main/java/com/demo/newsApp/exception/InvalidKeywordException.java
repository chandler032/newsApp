package com.demo.newsApp.exception;

public class InvalidKeywordException extends RuntimeException {
    public InvalidKeywordException(String message) {
        super(message);
    }
}
