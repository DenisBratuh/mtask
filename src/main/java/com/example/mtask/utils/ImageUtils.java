package com.example.mtask.utils;

import com.example.mtask.entity.LogoType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class ImageUtils {

    public static void checkForImage(MultipartFile file) {
        var contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }
    }

    public static String generateObjectName(MultipartFile file, LogoType logoType) {
        return logoType.name().toLowerCase() + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
    }

    public static void validateArguments(String fileName, LogoType logoType) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty.");
        }
        if (logoType == null) {
            throw new IllegalArgumentException("Logo type cannot be null.");
        }
    }
}