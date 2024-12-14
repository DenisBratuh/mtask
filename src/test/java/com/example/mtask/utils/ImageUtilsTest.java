package com.example.mtask.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.example.mtask.enums.LogoType;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class ImageUtilsTest {

    @Test
    void testCheckForImage_ValidImage() {
        // Given
        MockMultipartFile file = new MockMultipartFile("image", "image.png", "image/png", new byte[0]);

        // When & Then
        assertDoesNotThrow(() -> ImageUtils.checkForImage(file));
    }

    @Test
    void testCheckForImage_InvalidFileType() {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "text.txt", "text/plain", new byte[0]);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> ImageUtils.checkForImage(file)
        );
        assertEquals("Only image files are allowed.", exception.getMessage());
    }

    @Test
    void testGenerateObjectName_ValidInputs() {
        // Given
        MockMultipartFile file = new MockMultipartFile("image", "image.png", "image/png", new byte[0]);
        LogoType logoType = LogoType.PRODUCT;

        // When
        String objectName = ImageUtils.generateObjectName(file, logoType);

        // Then
        assertTrue(objectName.startsWith("product/"));
        assertTrue(objectName.contains("image.png"));
    }

    @Test
    void testValidateArguments_ValidInputs() {
        // Given
        String fileName = "file.png";

        // When & Then
        assertDoesNotThrow(() -> ImageUtils.validateFileName(fileName));
    }

    @Test
    void testValidateArguments_NullFileName() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> ImageUtils.validateFileName(null)
        );
        assertEquals("File name cannot be null or empty.", exception.getMessage());
    }
}
