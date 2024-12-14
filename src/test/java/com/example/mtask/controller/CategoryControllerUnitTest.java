package com.example.mtask.controller;

import com.example.mtask.config.SecurityConfig;
import com.example.mtask.dto.category.CategoryRcvDto;
import com.example.mtask.dto.category.CategorySendDto;
import com.example.mtask.service.inteface.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
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

@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
class CategoryControllerUnitTest {

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "regularUser")
    void testCreateCategory() throws Exception {
        var categorySendDto = new CategorySendDto();
        categorySendDto.setId(UUID.randomUUID());
        categorySendDto.setName("Test Category");

        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test content".getBytes());
        when(categoryService.createCategory(any(CategoryRcvDto.class))).thenReturn(categorySendDto);

        mockMvc.perform(multipart("/api/categories")
                        .file(file)
                        .param("name", "Test Category")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Category"));

        verify(categoryService, times(1)).createCategory(any(CategoryRcvDto.class));
    }

    @Test
    @WithMockUser(username = "regularUser")
    void testGetCategoryById() throws Exception {
        var categorySendDto = new CategorySendDto();
        categorySendDto.setId(UUID.randomUUID());
        categorySendDto.setName("Test Category");

        when(categoryService.getCategoryById(any(UUID.class))).thenReturn(categorySendDto);

        mockMvc.perform(get("/api/categories/{id}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Category"));

        verify(categoryService, times(1)).getCategoryById(any(UUID.class));
    }

    @Test
    @WithMockUser(username = "regularUser")
    void testDeleteCategory() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(any(UUID.class));
    }

    @Test
    @WithMockUser(username = "regularUser")
    void testGetPaginatedCategories() throws Exception {
        var categoryPage = new PageImpl<>(List.of(new CategorySendDto()));
        when(categoryService.getPaginatedCategories(anyInt(), anyInt())).thenReturn(categoryPage);

        mockMvc.perform(get("/api/categories")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(categoryService, times(1)).getPaginatedCategories(anyInt(), anyInt());
    }

    @Test
    @WithMockUser(username = "regularUser")
    void testGetCategoryLogo() throws Exception {
        when(categoryService.getCategoryLogo(any(UUID.class))).thenReturn("test image content".getBytes());

        mockMvc.perform(get("/api/categories/{id}/logo", UUID.randomUUID())
                        .accept(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));

        verify(categoryService, times(1)).getCategoryLogo(any(UUID.class));
    }

    @Test
    @WithMockUser(username = "regularUser")
    void testGetCategoryByIdNotFound() throws Exception {
        when(categoryService.getCategoryById(any(UUID.class))).thenThrow(new EntityNotFoundException("Category not found"));

        mockMvc.perform(get("/api/categories/{id}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found"));
    }

    @Test
    @WithMockUser(username = "regularUser")
    void testCreateCategoryWithInvalidFileType() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "not_an_image.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Invalid file content".getBytes()
        );

        doThrow(new IllegalArgumentException("Only image files are allowed"))
                .when(categoryService).createCategory(any(CategoryRcvDto.class));

        mockMvc.perform(multipart("/api/categories")
                        .file(invalidFile)
                        .param("name", "Test Category")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Only image files are allowed"));
    }
}
