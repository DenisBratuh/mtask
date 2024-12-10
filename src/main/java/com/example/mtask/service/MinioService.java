package com.example.mtask.service;

import com.example.mtask.entity.LogoType;
import com.example.mtask.exceptions.MinioOperationException;
import io.minio.*;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class MinioService {

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

    public String uploadLogo(MultipartFile file, LogoType logoType) throws FileUploadException {
        checkForImage(file);
        try (InputStream is = file.getInputStream()) {
            var objectName = generateObjectName(file, logoType);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(logoType.getBucketName())
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return objectName;
        } catch (IOException | MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new FileUploadException("Error during uploading logo image to MinIO.", e);
        }
    }

    public void deleteLogo(String fileName, LogoType logoType) {
        validateArguments(fileName, logoType);

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(logoType.getBucketName())
                            .object(fileName).build()
            );
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new MinioOperationException("Error occurred while deleting logo file: " + fileName, e);
        }
    }


    /**
     * Завантажити логотип з MinIO за вказаним шляхом.
     *
     * @param logoPath шлях до логотипу у MinIO
     * @return логотип як масив байт
     */
    public byte[] downloadLogo(String logoPath, LogoType logoType) {
        try (var inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(logoType.getBucketName())
                        .object(logoPath)
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

    /**
     * Перевірити, чи є файл зображенням.
     *
     */
    private void checkForImage(MultipartFile file) {
        var contentType = file.getContentType();
        if(!contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }
    }

    private String generateObjectName(MultipartFile file, LogoType logoType) {
        return logoType.name().toLowerCase() + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
    }

    private void validateArguments(String fileName, LogoType logoType) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty.");
        }

        if (logoType == null) {
            throw new IllegalArgumentException("Logo type cannot be null.");
        }
    }
}
