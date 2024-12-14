package com.example.mtask.service.impl;

import com.example.mtask.enums.LogoType;
import com.example.mtask.exception.MinioOperationException;
import com.example.mtask.service.imp.MinioService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    private MinioService minioService;

    @BeforeEach
    void setUp() {
        String testDefaultBucket = "test-bucket";
        minioService = new MinioService(minioClient, testDefaultBucket);
    }

    @Test
    void testUploadImageSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3, 4}
        );
        LogoType logoType = LogoType.CATEGORY;

        assertDoesNotThrow(() -> minioService.uploadImage(file, logoType));

        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testUploadImageFailure() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3, 4}
        );
        doThrow(new MinioOperationException("Error during uploading logo image to MinIO", new RuntimeException())).when(minioClient).putObject(any(PutObjectArgs.class));

        MinioOperationException exception = assertThrows(MinioOperationException.class, () -> minioService.uploadImage(file, LogoType.PRODUCT));

        assertTrue(exception.getMessage().contains("Error during uploading logo image to MinIO"));
    }

    @Test
    void testDownloadImageFailure() throws Exception {
        String imagePath = "test.jpg";

        doThrow(new MinioOperationException("Error occurred while downloading the logo from MinIO", new RuntimeException())).when(minioClient).getObject(any(GetObjectArgs.class));

        MinioOperationException exception = assertThrows(MinioOperationException.class, () -> minioService.downloadImage(imagePath));

        assertTrue(exception.getMessage().contains("Error occurred while downloading the logo from MinIO"));
    }

    @Test
    void testDeleteImageSuccess() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String fileName = "test.jpg";

        assertDoesNotThrow(() -> minioService.deleteImage(fileName));
        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void testDeleteImageFailure() throws Exception {
        String fileName = "test.jpg";

        doThrow(new MinioOperationException("Error occurred while deleting logo file", new RuntimeException())).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        MinioOperationException exception = assertThrows(MinioOperationException.class, () -> minioService.deleteImage(fileName));

        assertTrue(exception.getMessage().contains("Error occurred while deleting logo file"));
    }
}
