package com.example.mtask.assembler;

import com.example.mtask.dto.product.ProductSendDto;
import com.example.mtask.entity.Category;
import com.example.mtask.entity.Product;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductAsmTest {

    private final ProductAsm productAsm = new ProductAsm();

    @Test
    void testToDto_SingleEntity() {
        // Given
        UUID productId = UUID.randomUUID();
        String productName = "Test Product";
        String logoUrl = "logoPath";
        UUID categoryId = UUID.randomUUID();

        Category category = new Category();
        category.setId(categoryId);

        Product product = new Product();
        product.setId(productId);
        product.setName(productName);
        product.setLogoUrl(logoUrl);
        product.setCategory(category);

        // When
        ProductSendDto dto = productAsm.toDto(product);

        // Then
        assertNotNull(dto);
        assertEquals(productId, dto.getId());
        assertEquals(productName, dto.getName());
        assertEquals(logoUrl, dto.getLogoUrl());
        assertEquals(categoryId, dto.getCategoryId());
    }

    @Test
    void testToDto_NullEntityList() {
        // When
        List<ProductSendDto> result = productAsm.toDto((List<Product>) null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testToDto_EmptyEntityList() {
        // Given
        List<Product> emptyList = Collections.emptyList();

        // When
        List<ProductSendDto> result = productAsm.toDto(emptyList);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testToDto_ListWithEntities() {
        // Given
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        Category category = new Category();
        category.setId(UUID.randomUUID());

        Product product1 = new Product();
        product1.setId(productId1);
        product1.setName("Product 1");
        product1.setLogoUrl("logo1");
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setId(productId2);
        product2.setName("Product 2");
        product2.setLogoUrl("logo2");
        product2.setCategory(category);

        List<Product> products = Arrays.asList(product1, product2);

        // When
        List<ProductSendDto> result = productAsm.toDto(products);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        ProductSendDto dto1 = result.get(0);
        assertEquals(productId1, dto1.getId());
        assertEquals("Product 1", dto1.getName());
        assertEquals("logo1", dto1.getLogoUrl());
        assertEquals(category.getId(), dto1.getCategoryId());

        ProductSendDto dto2 = result.get(1);
        assertEquals(productId2, dto2.getId());
        assertEquals("Product 2", dto2.getName());
        assertEquals("logo2", dto2.getLogoUrl());
        assertEquals(category.getId(), dto2.getCategoryId());
    }

    @Test
    void testToDto_ListWithNullEntity() {
        // Given
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Product");
        product.setLogoUrl("logo");
        product.setCategory(new Category());

        List<Product> products = Arrays.asList(product, null);

        // When
        List<ProductSendDto> result = productAsm.toDto(products);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        ProductSendDto dto = result.get(0);
        assertEquals(product.getId(), dto.getId());
        assertEquals(product.getName(), dto.getName());
        assertEquals(product.getLogoUrl(), dto.getLogoUrl());
    }
}