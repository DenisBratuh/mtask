package com.example.mtask.service;

import com.example.mtask.dto.ProductRcvDto;
import com.example.mtask.dto.ProductSendDto;
import com.example.mtask.entity.Category;
import com.example.mtask.entity.Product;
import com.example.mtask.mapper.ProductAsm;
import com.example.mtask.repository.ProductRepository;
import com.example.mtask.service.imp.CategoryServiceImp;
import com.example.mtask.service.imp.MinioService;
import com.example.mtask.service.imp.ProductServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryServiceImp categoryServiceImp;

    @Mock
    private MinioService minioService;

    @Mock
    private ProductAsm productAsm;

    @InjectMocks
    private ProductServiceImp productServiceImp;

    @Test
    void testCreateProduct() {
        // Given
        UUID categoryId = UUID.randomUUID();
        String name = "Test Product";
        MultipartFile logoFile = mock(MultipartFile.class);
        Product product = new Product();
        ProductSendDto productDto = new ProductSendDto();

        when(categoryServiceImp.getCategoryEntityById(categoryId)).thenReturn(mock(Category.class));
        when(minioService.uploadImage(any(), any())).thenReturn("logoPath");
        when(productRepository.save(any())).thenReturn(product);
        when(productAsm.toDto(any(Product.class))).thenReturn(productDto);

        // When
        ProductSendDto result = productServiceImp.createProduct(name, categoryId, logoFile);

        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).save(any());
    }

    @Test
    void testGetProductDtoById() {
        // Given
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        ProductSendDto productDto = new ProductSendDto();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productAsm.toDto(product)).thenReturn(productDto);

        // When
        ProductSendDto result = productServiceImp.getProductDtoById(productId);

        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testUpdateProduct() {
        // Given
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        ProductRcvDto updateDto = new ProductRcvDto();
        updateDto.setName("Updated Name");
        updateDto.setCategoryId(UUID.randomUUID());
        updateDto.setClearLogo(true);

        Category mockCategory = new Category(); // Mocked valid category
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryServiceImp.getCategoryEntityById(updateDto.getCategoryId())).thenReturn(mockCategory);
        when(productRepository.save(product)).thenReturn(product);
        when(productAsm.toDto(product)).thenReturn(new ProductSendDto());

        // When
        ProductSendDto result = productServiceImp.updateProduct(productId, updateDto);

        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).save(product);
        verify(categoryServiceImp, times(1)).getCategoryEntityById(updateDto.getCategoryId());
    }

    @Test
    void testDeleteProduct() {
        // Given
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setLogoUrl("logoPath");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // When
        productServiceImp.deleteProduct(productId);

        // Then
        verify(minioService, times(1)).deleteImage(eq("logoPath"), any());
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testGetPaginatedProducts() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        List<Product> products = Collections.singletonList(new Product());
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productAsm.toDto(any(Product.class))).thenReturn(new ProductSendDto());

        // When
        Page<ProductSendDto> result = productServiceImp.getPaginatedProducts(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAll(pageable);
    }
}
