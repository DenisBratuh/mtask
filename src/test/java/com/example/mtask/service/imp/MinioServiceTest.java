package com.example.mtask.service.imp;

import com.example.mtask.enums.LogoType;
import com.example.mtask.exception.MinioOperationException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    private MinioService minioService;

    private static final MockMultipartFile VALID_FILE = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            new byte[]{1, 2, 3, 4}
    );

    private static final String TEST_FILE_NAME = "test.jpg";
    private static final String TEST_BUCKET = "test-bucket";

    @BeforeEach
    void setUp() {
        minioService = new MinioService(minioClient, TEST_BUCKET);
    }

    @Test
    void testUploadImageSuccess() throws Exception {
        assertDoesNotThrow(() -> minioService.uploadImage(VALID_FILE, LogoType.CATEGORY));

        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testUploadImageFailure() throws Exception {
        doThrow(new MinioOperationException("Error during uploading logo image to MinIO", new RuntimeException()))
                .when(minioClient).putObject(any(PutObjectArgs.class));

        var exception = assertThrows(MinioOperationException.class, () -> minioService.uploadImage(VALID_FILE, LogoType.PRODUCT));

        assertTrue(exception.getMessage().contains("Error during uploading logo image to MinIO"));
    }

    @Test
    void testDownloadImageFailure() throws Exception {
        doThrow(new MinioOperationException("Error occurred while downloading the logo from MinIO", new RuntimeException()))
                .when(minioClient).getObject(any(GetObjectArgs.class));

        var exception = assertThrows(MinioOperationException.class, () -> minioService.downloadImage(TEST_FILE_NAME));

        assertTrue(exception.getMessage().contains("Error occurred while downloading the logo from MinIO"));
    }

    @Test
    void testDeleteImageSuccess() throws Exception {
        assertDoesNotThrow(() -> minioService.deleteImage(TEST_FILE_NAME));
        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void testDeleteImageFailure() throws Exception {
        doThrow(new MinioOperationException("Error occurred while deleting logo file", new RuntimeException()))
                .when(minioClient).removeObject(any(RemoveObjectArgs.class));

        var exception = assertThrows(MinioOperationException.class, () -> minioService.deleteImage(TEST_FILE_NAME));

        assertTrue(exception.getMessage().contains("Error occurred while deleting logo file"));
    }
}
