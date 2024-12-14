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
        var productId = UUID.randomUUID();
        var productName = "Test Product";
        var logoUrl = "logoPath";
        var categoryId = UUID.randomUUID();

        var category = new Category();
        category.setId(categoryId);

        var product = new Product();
        product.setId(productId);
        product.setName(productName);
        product.setLogoUrl(logoUrl);
        product.setCategory(category);

        var dto = productAsm.toDto(product);

        assertNotNull(dto);
        assertEquals(productId, dto.getId());
        assertEquals(productName, dto.getName());
        assertEquals(logoUrl, dto.getLogoUrl());
        assertEquals(categoryId, dto.getCategoryId());
    }

    @Test
    void testToDto_NullEntityList() {
        var result = productAsm.toDto((List<Product>) null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testToDto_EmptyEntityList() {
        List<Product> emptyList = Collections.emptyList();

        List<ProductSendDto> result = productAsm.toDto(emptyList);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testToDto_ListWithEntities() {
        var productId1 = UUID.randomUUID();
        var productId2 = UUID.randomUUID();

        var category = new Category();
        category.setId(UUID.randomUUID());

        var product1 = new Product();
        product1.setId(productId1);
        product1.setName("Product 1");
        product1.setLogoUrl("logo1");
        product1.setCategory(category);

        var product2 = new Product();
        product2.setId(productId2);
        product2.setName("Product 2");
        product2.setLogoUrl("logo2");
        product2.setCategory(category);

        List<Product> products = Arrays.asList(product1, product2);

        List<ProductSendDto> result = productAsm.toDto(products);

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
        var product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Product");
        product.setLogoUrl("logo");
        product.setCategory(new Category());

        List<Product> products = Arrays.asList(product, null);

        List<ProductSendDto> result = productAsm.toDto(products);

        assertNotNull(result);
        assertEquals(1, result.size());

        var dto = result.get(0);
        assertEquals(product.getId(), dto.getId());
        assertEquals(product.getName(), dto.getName());
        assertEquals(product.getLogoUrl(), dto.getLogoUrl());
    }
}