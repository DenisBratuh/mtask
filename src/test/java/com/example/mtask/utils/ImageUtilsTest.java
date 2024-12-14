package com.example.mtask.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.example.mtask.enums.LogoType;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class ImageUtilsTest {

    @Test
    void testCheckForImage_ValidImage() {
        var file = new MockMultipartFile("image", "image.png", "image/png", new byte[0]);

        assertDoesNotThrow(() -> ImageUtils.checkForImage(file));
    }

    @Test
    void testCheckForImage_InvalidFileType() {
        var file = new MockMultipartFile("file", "text.txt", "text/plain", new byte[0]);

        var exception = assertThrows(
            IllegalArgumentException.class, 
            () -> ImageUtils.checkForImage(file)
        );
        assertEquals("Only image files are allowed.", exception.getMessage());
    }

    @Test
    void testGenerateObjectName_ValidInputs() {
        var file = new MockMultipartFile("image", "image.png", "image/png", new byte[0]);

        var objectName = ImageUtils.generateObjectName(file, LogoType.PRODUCT);

        assertTrue(objectName.startsWith("product/"));
        assertTrue(objectName.contains("image.png"));
    }

    @Test
    void testValidateArguments_ValidInputs() {
        String fileName = "file.png";

        assertDoesNotThrow(() -> ImageUtils.validateFileName(fileName));
    }

    @Test
    void testValidateArguments_NullFileName() {
        var exception = assertThrows(
            IllegalArgumentException.class, 
            () -> ImageUtils.validateFileName(null)
        );
        assertEquals("File name cannot be null or empty.", exception.getMessage());
    }
}
