package com.example.mtask.service;

import com.example.mtask.dto.CategoryDto;
import com.example.mtask.entity.Category;
import com.example.mtask.mapper.CategoryAsm;
import com.example.mtask.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

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

    @Test
    void testCreateCategory() {
        // Given
        String name = "Test Category";
        MultipartFile logoFile = mock(MultipartFile.class);
        Category category = new Category();
        CategoryDto categoryDto = new CategoryDto();

        when(logoFile.isEmpty()).thenReturn(false);
        when(minioService.uploadImage(any(MultipartFile.class), any())).thenReturn("logoPath");
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryAsm.toDto(category)).thenReturn(categoryDto);

        // When
        CategoryDto result = categoryServiceImp.createCategory(name, logoFile);

        // Then
        assertNotNull(result);
        verify(minioService, times(1)).uploadImage(any(MultipartFile.class), any());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testGetCategoryById() {
        // Given
        UUID id = UUID.randomUUID();
        Category category = new Category();
        CategoryDto categoryDto = new CategoryDto();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryAsm.toDto(category)).thenReturn(categoryDto);

        // When
        CategoryDto result = categoryServiceImp.getCategoryById(id);

        // Then
        assertNotNull(result);
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    void testGetCategoryById_NotFound() {
        // Given
        UUID id = UUID.randomUUID();

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImp.getCategoryById(id));
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteCategory() {
        // Given
        UUID id = UUID.randomUUID();
        Category category = new Category();
        category.setLogoUrl("logoPath");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        // When
        categoryServiceImp.deleteCategory(id);

        // Then
        verify(minioService, times(1)).deleteImage(eq("logoPath"), any());
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void testDeleteCategory_NoLogo() {
        // Given
        UUID id = UUID.randomUUID();
        Category category = new Category();
        category.setLogoUrl(null);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        // When
        categoryServiceImp.deleteCategory(id);

        // Then
        verify(minioService, never()).deleteImage(anyString(), any());
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void testGetPaginatedCategories() {
        // Given
        int page = 0;
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = new PageImpl<>(Collections.singletonList(new Category()));

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryAsm.toDto(any(Category.class))).thenReturn(new CategoryDto());

        // When
        Page<CategoryDto> result = categoryServiceImp.getPaginatedCategories(page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetCategoryLogo() {
        // Given
        UUID id = UUID.randomUUID();
        Category category = new Category();
        category.setLogoUrl("logoPath");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setLogoUrl("logoPath");
        when(categoryAsm.toDto(any(Category.class))).thenReturn(categoryDto);

        when(minioService.downloadImage(eq("logoPath"), any())).thenReturn(new byte[]{1, 2, 3});

        // When
        byte[] result = categoryServiceImp.getCategoryLogo(id);

        // Then
        assertNotNull(result);
        assertEquals(3, result.length);
        verify(minioService, times(1)).downloadImage(eq("logoPath"), any());
    }

    @Test
    void testGetCategoryLogo_NoLogo() {
        // Given
        UUID id = UUID.randomUUID();
        Category category = new Category();
        category.setLogoUrl(null);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryAsm.toDto(category)).thenReturn(new CategoryDto());

        // When
        byte[] result = categoryServiceImp.getCategoryLogo(id);

        // Then
        assertNotNull(result);
        assertEquals(0, result.length);
        verify(minioService, never()).downloadImage(anyString(), any());
    }
}