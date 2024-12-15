package com.example.mtask.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class for initializing and setting up the MinIO client.
 * <p>
 * This class reads the MinIO configuration properties (endpoint, access key, secret key)
 * from the application configuration and provides a {@link MinioClient} bean
 * to interact with MinIO.
 * </p>
 */
@Configuration
@Profile("!test")
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}