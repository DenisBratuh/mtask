package com.example.mtask.service.imp;

import com.example.mtask.enums.LogoType;
import com.example.mtask.exception.MinioOperationException;
import com.example.mtask.service.inteface.ImageStorageService;
import com.example.mtask.utils.ImageUtils;
import io.minio.*;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinioService implements ImageStorageService {

    private static final int BUFFER_SIZE = 1024;

    private final MinioClient minioClient;
    private final String defaultBucket;

    public MinioService(MinioClient minioClient, @Value("${minio.default-bucket}") String defaultBucket) {
        this.minioClient = minioClient;
        this.defaultBucket = defaultBucket;
    }

    @PostConstruct
    public void initializeDefaultBucket() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(defaultBucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(defaultBucket).build());
            }
        } catch (Exception e) {
            throw new MinioOperationException("Error initializing default bucket: " + defaultBucket, e);
        }
    }

    @Override
    public String uploadImage(MultipartFile file, LogoType logoType) {
        ImageUtils.checkForImage(file);
        try (InputStream is = file.getInputStream()) {
            var objectName = ImageUtils.generateObjectName(file, logoType);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .object(objectName)
                            .bucket(defaultBucket)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return objectName;
        } catch (IOException | MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new MinioOperationException("Error during uploading logo image to MinIO.", e);
        }
    }

    @Override
    public void deleteImage(String fileName) {
        ImageUtils.validateFileName(fileName);

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(defaultBucket)
                            .object(fileName).build()
            );
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new MinioOperationException("Error occurred while deleting logo file: " + fileName, e);
        }
    }

    @Override
    public byte[] downloadImage(String imagePath) {
        try (
                GetObjectResponse inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .object(imagePath)
                        .bucket(defaultBucket)
                        .build())) {

            var byteArrayOutputStream = new ByteArrayOutputStream();
            var buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, length);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new MinioOperationException("Error occurred while downloading the logo from MinIO.", e);
        }
    }
}
