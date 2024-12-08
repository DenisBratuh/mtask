package com.example.mtask.service;

import com.example.mtask.entity.LogoType;
import com.example.mtask.exceptions.LogoDeletionException;
import com.example.mtask.exceptions.LogoDownloadException;
import io.minio.*;
import io.minio.errors.MinioException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
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

    //TODO separate class with method to return bean?
    private final MinioClient minioClient;

    public MinioService() {
        this.minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("ROOTNAME", "CHANGEME123")
                .build();

        initializeBuckets();
    }

    public String uploadLogo(MultipartFile file, LogoType logoType) throws FileUploadException {
        if (!isImage(file)) {
            throw new IllegalArgumentException("Тільки зображення дозволяються для завантаження.");
        }

        try (InputStream is = file.getInputStream()) {
            //TODO generate name separate method?
            var objectName = logoType.name().toLowerCase() + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
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
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty.");
        }

        if (logoType == null) {
            throw new IllegalArgumentException("Logo type cannot be null.");
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(logoType.getBucketName())
                            .object(fileName).build()
            );
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new LogoDeletionException("Error occurred while deleting logo file: " + fileName, e);
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
            var buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, length);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new LogoDownloadException("Error occurred while downloading the logo from MinIO.", e);
        }
    }

    /**
     * Перевірити, чи є файл зображенням.
     *
     * @return true, якщо це зображення
     */
    private boolean isImage(MultipartFile file) {
        var contentType = file.getContentType();
        return contentType.startsWith("image/");
    }

    private void initializeBuckets() {
        try {
            for (var bucketName : LogoType.getAllBucketNames()) {
                if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                }
            }
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error initializing Minio buckets", e);
        }
    }
}
