package com.example.mtask.exception;

/**
 * Exception thrown for any errors related to MinIO operations.
 */
public class MinioOperationException extends RuntimeException {

    /**
     * Constructs a new MinioOperationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public MinioOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}