package com.example.mtask.utils;

import com.example.mtask.enums.LogoType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Utility class for handling image-related operations.
 */
public class ImageUtils {

    /**
     * Validates if the provided file is an image.
     *
     * @param file the {@link MultipartFile} to check.
     * @throws IllegalArgumentException if the file is not an image.
     */
    public static void checkForImage(MultipartFile file) {
        var contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }
    }

    /**
     * Generates a unique object name for storing an image file in a specific directory.
     *
     * @param file     the {@link MultipartFile} representing the image.
     * @param logoType the {@link LogoType} representing the category of the logo (e.g., product or category).
     * @return a string representing the generated object name.
     */
    public static String generateObjectName(MultipartFile file, LogoType logoType) {
        return logoType.name().toLowerCase() + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
    }

    /**
     * Validates the provided file name.
     *
     * @param fileName the name of the file to validate.
     * @throws IllegalArgumentException if the file name is null or empty.
     */
    public static void validateFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty.");
        }
    }
}