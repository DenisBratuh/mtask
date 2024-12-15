package com.example.mtask.service.imp;

import com.example.mtask.assembler.ProductAsm;
import com.example.mtask.dto.product.ProductCreateDto;
import com.example.mtask.dto.product.ProductSendDto;
import com.example.mtask.dto.product.ProductUpdateDto;
import com.example.mtask.entity.Category;
import com.example.mtask.entity.Product;
import com.example.mtask.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static com.example.mtask.enums.LogoType.PRODUCT;
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

    private UUID productId;
    private Product product;
    private ProductSendDto productDto;
    private Category category;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = new Product();
        productDto = new ProductSendDto();
        category = new Category();
    }

    @Test
    void testCreateProduct() {
        var categoryId = UUID.randomUUID();
        var name = "Test Product";
        var logoFile = mock(MultipartFile.class);

        var createDto = new ProductCreateDto();
        createDto.setName(name);
        createDto.setCategoryId(categoryId);
        createDto.setLogoFile(logoFile);

        when(categoryServiceImp.getCategoryEntityById(categoryId)).thenReturn(category);
        when(logoFile.isEmpty()).thenReturn(false);
        when(minioService.uploadImage(logoFile, PRODUCT)).thenReturn("logoPath");
        when(productRepository.save(any())).thenReturn(product);
        when(productAsm.toDto(product)).thenReturn(productDto);

        var result = productServiceImp.createProduct(createDto);

        assertNotNull(result);
        verify(categoryServiceImp, times(1)).getCategoryEntityById(categoryId);
        verify(minioService, times(1)).uploadImage(logoFile, PRODUCT);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testGetProductDtoById() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productAsm.toDto(product)).thenReturn(productDto);

        var result = productServiceImp.getProductDtoById(productId);

        assertNotNull(result);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testUpdateProduct() {
        var updateDto = new ProductUpdateDto();
        updateDto.setName("Updated Name");
        updateDto.setCategoryId(UUID.randomUUID());
        updateDto.setClearLogo(true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryServiceImp.getCategoryEntityById(updateDto.getCategoryId())).thenReturn(category);
        when(productRepository.save(product)).thenReturn(product);
        when(productAsm.toDto(product)).thenReturn(productDto);

        var result = productServiceImp.updateProduct(productId, updateDto);

        assertNotNull(result);
        verify(productRepository, times(1)).save(product);
        verify(categoryServiceImp, times(1)).getCategoryEntityById(updateDto.getCategoryId());
    }

    @Test
    void testDeleteProduct() {
        product.setLogoUrl("logoPath");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productServiceImp.deleteProduct(productId);

        verify(minioService, times(1)).deleteImage(eq("logoPath"));
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testGetPaginatedProducts() {
        var pageable = PageRequest.of(0, 10);
        var products = Collections.singletonList(product);
        var productPage = new PageImpl<>(products);

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productAsm.toDto(any(Product.class))).thenReturn(productDto);

        var result = productServiceImp.getPaginatedProducts(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAll(pageable);
    }
}