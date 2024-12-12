package com.example.mtask.assembler;

import com.example.mtask.dto.category.CategorySendDto;
import com.example.mtask.dto.product.ProductSendDto;
import com.example.mtask.entity.Category;
import com.example.mtask.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryAsmTest {

    @Mock
    private ProductAsm productAsm;

    @InjectMocks
    private CategoryAsm categoryAsm;

    @Test
    void testToDto() {
        // Given
        UUID categoryId = UUID.randomUUID();
        String categoryName = "Test Category";
        String logoUrl = "http://example.com/logo.png";

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        category.setLogoUrl(logoUrl);
        category.setProducts(List.of(new Product()));

        List<ProductSendDto> productDtos = List.of(new ProductSendDto());
        when(productAsm.toDto(anyList())).thenReturn(productDtos);

        // When
        CategorySendDto result = categoryAsm.toDto(category);

        // Then
        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals(categoryName, result.getName());
        assertEquals(logoUrl, result.getLogoUrl());
        assertEquals(productDtos, result.getProductDtoList());

        verify(productAsm, times(1)).toDto(category.getProducts());
    }
}