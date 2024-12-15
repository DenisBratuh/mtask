package com.example.mtask.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;

@TestConfiguration
@Profile("test")
public class MinioTestConfig {

    private static final int TEST_PORT = 9000;
    private static final GenericContainer<?> MINIO_CONTAINER;

    static {
        MINIO_CONTAINER = new GenericContainer<>("quay.io/minio/minio:latest")
                .withEnv("MINIO_ROOT_USER", "TESTUSER")
                .withEnv("MINIO_ROOT_PASSWORD", "TESTPASSWORD")
                .withCommand("server /data")
                .withExposedPorts(TEST_PORT);
        MINIO_CONTAINER.start();
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://" + MINIO_CONTAINER.getHost() + ":" + MINIO_CONTAINER.getMappedPort(TEST_PORT))
                .credentials("TESTUSER", "TESTPASSWORD")
                .build();
    }

    @BeforeAll
    public static void setupBuckets() throws Exception {
        var minioClient = MinioClient.builder()
                .endpoint("http://" + MINIO_CONTAINER.getHost() + ":" + MINIO_CONTAINER.getMappedPort(TEST_PORT))
                .credentials("TESTUSER", "TESTPASSWORD")
                .build();

        String testBucket = "test-bucket";

        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(testBucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(testBucket).build());
        }
    }
}
