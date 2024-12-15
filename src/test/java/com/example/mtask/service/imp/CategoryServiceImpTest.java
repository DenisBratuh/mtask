package com.example.mtask.service.imp;

import com.example.mtask.assembler.CategoryAsm;
import com.example.mtask.dto.category.CategoryRcvDto;
import com.example.mtask.dto.category.CategorySendDto;
import com.example.mtask.entity.Category;
import com.example.mtask.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
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

import static com.example.mtask.enums.LogoType.CATEGORY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImpTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryAsm categoryAsm;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private CategoryServiceImp categoryServiceImp;

    private static final String TEST_LOGO_PATH = "logoPath";
    private static final UUID CATEGORY_ID = UUID.randomUUID();
    private static final Category TEST_CATEGORY = new Category();

    @BeforeEach
    void setUp() {
        TEST_CATEGORY.setLogoUrl(TEST_LOGO_PATH);
    }

    @Test
    void testCreateCategory() {
        var categoryRcvDto = new CategoryRcvDto();
        categoryRcvDto.setName("Test Category");
        categoryRcvDto.setFile(mock(MultipartFile.class));

        var savedCategory = new Category();
        var categorySendDto = new CategorySendDto();

        when(categoryRcvDto.getFile().isEmpty()).thenReturn(false);
        when(minioService.uploadImage(any(MultipartFile.class), eq(CATEGORY))).thenReturn(TEST_LOGO_PATH);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryAsm.toDto(savedCategory)).thenReturn(categorySendDto);

        var result = categoryServiceImp.createCategory(categoryRcvDto);

        assertNotNull(result);
        verify(minioService, times(1)).uploadImage(any(MultipartFile.class), eq(CATEGORY));
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testGetCategoryById() {
        var categorySendDto = new CategorySendDto();

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(TEST_CATEGORY));
        when(categoryAsm.toDto(TEST_CATEGORY)).thenReturn(categorySendDto);

        var result = categoryServiceImp.getCategoryById(CATEGORY_ID);

        assertNotNull(result);
        verify(categoryRepository).findById(CATEGORY_ID);
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryServiceImp.getCategoryById(CATEGORY_ID));
        verify(categoryRepository).findById(CATEGORY_ID);
    }

    @Test
    void testDeleteCategory() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(TEST_CATEGORY));

        categoryServiceImp.deleteCategory(CATEGORY_ID);

        verify(minioService).deleteImage(TEST_LOGO_PATH);
        verify(categoryRepository).delete(TEST_CATEGORY);
    }

    @Test
    void testDeleteCategory_NoLogo() {
        TEST_CATEGORY.setLogoUrl(null);

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(TEST_CATEGORY));

        categoryServiceImp.deleteCategory(CATEGORY_ID);

        verify(minioService, never()).deleteImage(anyString());
        verify(categoryRepository).delete(TEST_CATEGORY);
    }

    @Test
    void testGetPaginatedCategories() {
        var pageable = PageRequest.of(0, 10);
        var categoryPage = new PageImpl<>(Collections.singletonList(TEST_CATEGORY));
        var categorySendDto = new CategorySendDto();

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryAsm.toDto(any(Category.class))).thenReturn(categorySendDto);

        var result = categoryServiceImp.getPaginatedCategories(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(categoryRepository).findAll(pageable);
    }

    @Test
    void testGetCategoryLogo() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(TEST_CATEGORY));
        when(minioService.downloadImage(TEST_LOGO_PATH)).thenReturn(new byte[]{1, 2, 3});

        var result = categoryServiceImp.getCategoryLogo(CATEGORY_ID);

        assertNotNull(result);
        assertEquals(3, result.length);
        verify(minioService).downloadImage(TEST_LOGO_PATH);
    }

    @Test
    void testGetCategoryLogo_NoLogo() {
        TEST_CATEGORY.setLogoUrl(null);

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(TEST_CATEGORY));

        var result = categoryServiceImp.getCategoryLogo(CATEGORY_ID);

        assertNotNull(result);
        assertEquals(0, result.length);
        verify(minioService, never()).downloadImage(anyString());
    }
}