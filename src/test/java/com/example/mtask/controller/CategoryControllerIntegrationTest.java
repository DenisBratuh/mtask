package com.example.mtask.controller;

import com.example.mtask.dto.category.CategorySendDto;
import com.example.mtask.entity.Category;
import com.example.mtask.repository.CategoryRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CategoryControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final String BASE_URL = "http://localhost:";
    private String categoryApiUrl;

    @BeforeEach
    void setUp() {
        restTemplate = restTemplate.withBasicAuth("regularUser", "password");
        categoryApiUrl = BASE_URL + port + "/api/categories";
    }

    @AfterEach
    void cleanUpDatabase() {
        categoryRepository.deleteAll();
    }

    private HttpEntity<LinkedMultiValueMap<String, Object>> createMultipartRequest(String name, byte[] fileContent, String fileName) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var multipartBody = new LinkedMultiValueMap<String, Object>();
        multipartBody.add("name", name);
        if (fileContent != null) {
            multipartBody.add("file", new ByteArrayResource(fileContent) {
                @NotNull
                @Override
                public String getFilename() {
                    return fileName;
                }
            });
        }

        return new HttpEntity<>(multipartBody, headers);
    }

    @Test
    void testCreateCategory() {
        var request = createMultipartRequest("Test Category", "test content".getBytes(), "test.jpg");

        var response = restTemplate.postForEntity(categoryApiUrl, request, CategorySendDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test Category");

        var savedCategory = categoryRepository.findByName("Test Category");
        assertThat(savedCategory).isPresent();
    }

    @Test
    void testGetCategoryById() {
        var category = categoryRepository.save(new Category(null, "Existing Category", null));

        var response = restTemplate.getForEntity(categoryApiUrl + "/" + category.getId(), CategorySendDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Existing Category");
    }

    @Test
    void testDeleteCategory() {
        var category = categoryRepository.save(new Category(null, "Category to Delete", null));

        restTemplate.delete(categoryApiUrl + "/" + category.getId());

        assertThat(categoryRepository.findById(category.getId())).isEmpty();
    }

    @Test
    void testGetPaginatedCategories() {
        categoryRepository.saveAll(List.of(
                new Category(null, "Category 1", null),
                new Category(null, "Category 2", null),
                new Category(null, "Category 3", null)
        ));

        var response = restTemplate.getForEntity(categoryApiUrl + "?page=0&size=2", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Category 1", "Category 2");
    }

    @Test
    void testGetCategoryNotFound() {
        var randomId = UUID.randomUUID();

        var response = restTemplate.getForEntity(categoryApiUrl + "/" + randomId, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Category not found");
    }

    @Test
    void testCreateCategoryWithLargeFile() {
        byte[] largeContent = new byte[2 * 1024 * 1024];
        var request = createMultipartRequest("Large File", largeContent, "large_file.jpg");

        var response = restTemplate.postForEntity(categoryApiUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        assertThat(response.getBody()).contains("Payload Too Large");
    }

    @Test
    void testGetCategoryLogo_LogoExists() {
        var createRequest = createMultipartRequest("Category with Logo", "image content".getBytes(), "test.jpg");
        var createResponse = restTemplate.postForEntity(categoryApiUrl, createRequest, CategorySendDto.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var createdCategory = createResponse.getBody();
        assertThat(createdCategory).isNotNull();

        var response = restTemplate.getForEntity(categoryApiUrl + "/" + createdCategory.getId() + "/logo", byte[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_JPEG);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testGetCategoryLogo_NoLogo() {
        var category = categoryRepository.save(new Category(null, "Category without Logo", null));

        var response = restTemplate.getForEntity(categoryApiUrl + "/" + category.getId() + "/logo", Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testGetCategoryLogo_CategoryNotFound() {
        var randomId = UUID.randomUUID();

        var response = restTemplate.getForEntity(categoryApiUrl + "/" + randomId + "/logo", Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}