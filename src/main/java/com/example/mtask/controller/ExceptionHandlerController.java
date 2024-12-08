package com.example.mtask.controller;

import com.example.mtask.exceptions.LogoDeletionException;
import com.example.mtask.exceptions.LogoDownloadException;
import io.minio.errors.MinioException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;


@RestControllerAdvice
public class ExceptionHandlerController {

    //TODO check
    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<Object> handleSizeLimitExceededException(SizeLimitExceededException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    //TODO check
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Object> handleFileUploadException(FileUploadException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(LogoDeletionException.class)
    public ResponseEntity<Object> handleLogoDeletionException(LogoDeletionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(LogoDownloadException.class)
    public ResponseEntity<Object> handleLogoDownloadException(LogoDownloadException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }



//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
//        return new ResponseEntity<>(new ErrorResponse("Invalid input", ex.getMessage()), HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(MinioException.class)
//    public ResponseEntity<ErrorResponse> handleMinioException(MinioException ex) {
//        return new ResponseEntity<>(new ErrorResponse("Minio error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @ExceptionHandler(IOException.class)
//    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
//        return new ResponseEntity<>(new ErrorResponse("IO error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
