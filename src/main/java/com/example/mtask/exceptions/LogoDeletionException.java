package com.example.mtask.exceptions;

public class LogoDeletionException extends RuntimeException {
    public LogoDeletionException(String message) {
        super(message);
    }

    public LogoDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}