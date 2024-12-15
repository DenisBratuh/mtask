package com.example.mtask.controller;

import com.example.mtask.config.MinioTestConfig;
import com.example.mtask.dto.product.ProductSendDto;
import com.example.mtask.entity.Product;
import com.example.mtask.entity.Category;
import com.example.mtask.repository.CategoryRepository;
import com.example.mtask.repository.ProductRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(MinioTestConfig.class)
class ProductControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final String BASE_URL = "http://localhost:";
    private String productApiUrl;

    @BeforeEach
    void setUp() {
        restTemplate = restTemplate.withBasicAuth("editorUser", "password");
        productApiUrl = BASE_URL + port + "/api/products";
    }

    @AfterEach
    void cleanUpDatabase() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    private HttpEntity<LinkedMultiValueMap<String, Object>> createMultipartRequest(String name, UUID categoryId, byte[] fileContent, String fileName) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var multipartBody = new LinkedMultiValueMap<String, Object>();
        multipartBody.add("name", name);
        multipartBody.add("categoryId", categoryId.toString());
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
    void testCreateProduct() {
        var category = categoryRepository.save(new Category(null, "Category for Product", null));
        var request = createMultipartRequest("Test Product", category.getId(), "test content".getBytes(), "test.jpg");

        var response = restTemplate.postForEntity(productApiUrl, request, ProductSendDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test Product");

        var savedProduct = productRepository.findById(response.getBody().getId());
        assertThat(savedProduct).isPresent();
        assertThat(savedProduct.get().getCategory().getId()).isEqualTo(category.getId());
    }

    @Test
    void testGetProductById() {
        var category = categoryRepository.save(new Category(null, "Category for Product", null));
        var product = productRepository.save(new Product(null, "Existing Product", "logoPath", category));

        var response = restTemplate.getForEntity(productApiUrl + "/" + product.getId(), ProductSendDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Existing Product");
    }

    @Test
    void testUpdateProduct() {
        var category = categoryRepository.save(new Category(null, "Category for Product", null));
        var product = productRepository.save(new Product(null, "Old Product Name", "logoPath", category));

        var request = createMultipartRequest("Updated Product", category.getId(), "new content".getBytes(), "updated.jpg");

        restTemplate.put(productApiUrl + "/" + product.getId(), request);

        var updatedProduct = productRepository.findById(product.getId());
        assertThat(updatedProduct).isPresent();
        assertThat(updatedProduct.get().getName()).isEqualTo("Updated Product");
    }

    @Test
    void testDeleteProduct() {
        var category = categoryRepository.save(new Category(null, "Category for Product", null));
        var product = productRepository.save(new Product(null, "Product to Delete", "logoPath", category));

        restTemplate.delete(productApiUrl + "/" + product.getId());

        var deletedProduct = productRepository.findById(product.getId());
        assertThat(deletedProduct).isEmpty();
    }

    @Test
    void testGetPaginatedProducts() {
        var category = categoryRepository.save(new Category(null, "Category for Products", null));
        productRepository.saveAll(List.of(
                new Product(null, "Product 1", null, category),
                new Product(null, "Product 2", null, category),
                new Product(null, "Product 3", null, category)
        ));

        var response = restTemplate.getForEntity(productApiUrl + "?page=0&size=2", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Product 1", "Product 2");
    }

    @Test
    void testCreateProductWithLargeFile() {
        var category = categoryRepository.save(new Category(null, "Category for Large Product", null));

        byte[] largeContent = new byte[2 * 1024 * 1024];
        var request = createMultipartRequest("Large Product", category.getId(), largeContent, "large_file.jpg");

        var response = restTemplate.postForEntity(productApiUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        assertThat(response.getBody()).contains("Payload Too Large");
    }

    @Test
    void testGetProductNotFound() {
        var randomId = UUID.randomUUID();

        var response = restTemplate.getForEntity(productApiUrl + "/" + randomId, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Product not found");
    }

    @Test
    void testUpdateProductWithLogoDeletion() {
        var category = categoryRepository.save(new Category(null, "Category for Product", null));
        var product = productRepository.save(new Product(null, "Product with Logo", "logoPath", category));

        assertThat(product.getLogoUrl()).isNotNull();

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var multipartBody = new LinkedMultiValueMap<String, Object>();
        multipartBody.add("name", "Updated Product");
        multipartBody.add("categoryId", category.getId().toString());
        multipartBody.add("clearLogo", "true");

        var request = new HttpEntity<>(multipartBody, headers);

        restTemplate.put(productApiUrl + "/" + product.getId(), request);

        var updatedProduct = productRepository.findById(product.getId());
        assertThat(updatedProduct).isPresent();
        assertThat(updatedProduct.get().getName()).isEqualTo("Updated Product");
        assertThat(updatedProduct.get().getLogoUrl()).isNull();
    }
}
