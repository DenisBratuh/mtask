package com.example.mtask.service;

import com.example.mtask.entity.LogoType;
import com.example.mtask.exceptions.MinioOperationException;
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

    //TODO ?
//    @Test
//    void testDownloadImageSuccess() throws Exception {
//        byte[] expectedBytes = {1, 2, 3, 4, 5};
//        InputStream objectStream = new ByteArrayInputStream(expectedBytes);
//
//        // Mock the minioClient to return the mocked InputStream
//        GetObjectResponse response = mock(GetObjectResponse.class);
//        when(response.read(Mockito.any(byte[].class))).thenAnswer(
//
//        when(minioClient.getObject(Mockito.any(GetObjectArgs.class))).then(invocation -> {
//            GetObjectArgs getObjectRequest = invocation.getArgument(0);
//            assertEquals("test-bucket", getObjectRequest.bucket());
//            assertEquals(IMAGE_PATH, getObjectRequest.object());
//
//            return new ResponseInputStream<>(
//                    GetObjectResponse.builder().build(), AbortableInputStream.create(objectStream));
//        });
//
//        // Act: Call the downloadImage method
//        byte[] result = minioService.downloadImage(IMAGE_PATH, LogoType.PRODUCT);
//
//        // Assert: Ensure the result is as expected
//        assertArrayEquals(expectedBytes, result);
//        verify(minioClient, times(1)).getObject(any(GetObjectArgs.class));
//    }

    @Test
    void testDownloadImageFailure() throws Exception {
        String imagePath = "test.jpg";
        LogoType logoType = LogoType.PRODUCT;

        doThrow(new MinioOperationException("Error occurred while downloading the logo from MinIO", new RuntimeException())).when(minioClient).getObject(any(GetObjectArgs.class));

        MinioOperationException exception = assertThrows(MinioOperationException.class, () -> minioService.downloadImage(imagePath, logoType));

        assertTrue(exception.getMessage().contains("Error occurred while downloading the logo from MinIO"));
    }

    @Test
    void testDeleteImageSuccess() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String fileName = "test.jpg";
        LogoType logoType = LogoType.CATEGORY;

        assertDoesNotThrow(() -> minioService.deleteImage(fileName, logoType));
        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void testDeleteImageFailure() throws Exception {
        String fileName = "test.jpg";

        doThrow(new MinioOperationException("Error occurred while deleting logo file", new RuntimeException())).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        MinioOperationException exception = assertThrows(MinioOperationException.class, () -> minioService.deleteImage(fileName, LogoType.PRODUCT));

        assertTrue(exception.getMessage().contains("Error occurred while deleting logo file"));
    }
}
