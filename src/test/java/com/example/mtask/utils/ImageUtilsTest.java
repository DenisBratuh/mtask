package com.example.mtask.utils;

import com.example.mtask.enums.LogoType;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class ImageUtilsTest {

    private static final MockMultipartFile VALID_IMAGE_FILE =
            new MockMultipartFile("image", "image.png", "image/png", new byte[0]);
    private static final MockMultipartFile INVALID_FILE_TYPE =
            new MockMultipartFile("file", "text.txt", "text/plain", new byte[0]);

    private static final String VALID_FILE_NAME = "file.png";

    @Test
    void testCheckForImage_ValidImage() {
        assertDoesNotThrow(() -> ImageUtils.checkForImage(VALID_IMAGE_FILE));
    }

    @Test
    void testCheckForImage_InvalidFileType() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> ImageUtils.checkForImage(INVALID_FILE_TYPE)
        );
        assertEquals("Only image files are allowed.", exception.getMessage());
    }

    @Test
    void testGenerateObjectName_ValidInputs() {
        var objectName = ImageUtils.generateObjectName(VALID_IMAGE_FILE, LogoType.PRODUCT);

        assertTrue(objectName.startsWith("product/"));
        assertTrue(objectName.contains("image.png"));
    }

    @Test
    void testValidateArguments_ValidInputs() {
        assertDoesNotThrow(() -> ImageUtils.validateFileName(VALID_FILE_NAME));
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