package com.example.mtask.controller;

import com.example.mtask.config.SecurityConfig;
import com.example.mtask.dto.product.ProductCreateDto;
import com.example.mtask.dto.product.ProductSendDto;
import com.example.mtask.dto.product.ProductUpdateDto;
import com.example.mtask.service.inteface.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerUnitTest {

    @MockitoBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "regularUser")
    void testCreateProduct() throws Exception {
        var productSendDto = new ProductSendDto();
        productSendDto.setId(UUID.randomUUID());
        productSendDto.setName("Test Product");

        var file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test content".getBytes());
        when(productService.createProduct(any(ProductCreateDto.class))).thenReturn(productSendDto);

        mockMvc.perform(multipart("/api/products")
                        .file(file)
                        .param("name", "Test Product")
                        .param("categoryId", UUID.randomUUID().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).createProduct(any(ProductCreateDto.class));
    }

    @Test
    @WithMockUser(username = "regularUser")
    void testGetProduct() throws Exception {
        var productSendDto = new ProductSendDto();
        productSendDto.setId(UUID.randomUUID());
        productSendDto.setName("Test Product");

        when(productService.getProductDtoById(any(UUID.class))).thenReturn(productSendDto);

        mockMvc.perform(get("/api/products/{id}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).getProductDtoById(any(UUID.class));
    }

    @Test
    @WithMockUser(username = "editorUser", roles = "EDITOR")
    void testUpdateProduct() throws Exception {
        var productSendDto = new ProductSendDto();
        productSendDto.setId(UUID.randomUUID());
        productSendDto.setName("Updated Product");

        var file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test content".getBytes());
        when(productService.updateProduct(any(UUID.class), any(ProductUpdateDto.class))).thenReturn(productSendDto);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/products/{id}", UUID.randomUUID())
                        .file(file)
                        .param("name", "Updated Product")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"));

        verify(productService, times(1)).updateProduct(any(UUID.class), any(ProductUpdateDto.class));
    }

    @Test
    @WithMockUser(username = "regularUser")
    void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(any(UUID.class));
    }

    @Test
    @WithMockUser(username = "regularUser")
    void testSearchProducts() throws Exception {
        var productSendDto = new ProductSendDto();
        productSendDto.setId(UUID.randomUUID());
        productSendDto.setName("Test Product");

        when(productService.searchProductsByName(anyString())).thenReturn(List.of(productSendDto));

        mockMvc.perform(get("/api/products/search")
                        .param("name", "Test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));

        verify(productService, times(1)).searchProductsByName(anyString());
    }

    @Test
    @WithMockUser(username = "regularUser")
    void testGetProductLogo() throws Exception {
        when(productService.getProductLogo(any(UUID.class))).thenReturn("test image content".getBytes());

        mockMvc.perform(get("/api/products/{id}/logo", UUID.randomUUID())
                        .accept(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));

        verify(productService, times(1)).getProductLogo(any(UUID.class));
    }

    @Test
    @WithMockUser(username = "regularUser")
    void testGetPaginatedProducts() throws Exception {
        var productPage = new PageImpl<>(List.of(new ProductSendDto()));
        when(productService.getPaginatedProducts(anyInt(), anyInt())).thenReturn(productPage);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService, times(1)).getPaginatedProducts(anyInt(), anyInt());
    }
}