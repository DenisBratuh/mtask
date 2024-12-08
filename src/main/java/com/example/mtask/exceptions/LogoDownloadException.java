package com.example.mtask.exceptions;

public class LogoDownloadException extends RuntimeException {
    public LogoDownloadException(String message) {
        super(message);
    }

    public LogoDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}