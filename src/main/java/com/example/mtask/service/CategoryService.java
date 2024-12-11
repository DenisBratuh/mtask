package com.example.mtask.service;

import com.example.mtask.dto.CategoryDto;
import com.example.mtask.entity.Category;
import com.example.mtask.mapper.CategoryAsm;
import com.example.mtask.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.example.mtask.entity.LogoType.CATEGORY;
import static com.example.mtask.entity.LogoType.PRODUCT;

@Service
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryAsm asm;
    private final MinioService minioService;

    @Autowired
    public CategoryService(CategoryRepository repository, CategoryAsm asm, MinioService minioService) {
        this.repository = repository;
        this.asm = asm;
        this.minioService = minioService;
    }

    @Transactional
    public CategoryDto createCategory(String name, MultipartFile logoFile) {
        String logoUrl = null;

        if (logoFile != null && !logoFile.isEmpty()) {
            logoUrl = minioService.uploadImage(logoFile, CATEGORY);
        }

        var category = new Category();
        category.setName(name);
        category.setLogoUrl(logoUrl);

        category = repository.save(category);
        return asm.toDto(category);
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(UUID id) {
        var foundEntity = getCategoryByIdInternal(id);
        return asm.toDto(foundEntity);
    }

    @Transactional(readOnly = true)
    public Category getCategoryEntityById(UUID id) {
        return getCategoryByIdInternal(id);
    }

    //    // Пошук категорій за назвою
//    public List<Category> searchCategoriesByName(String name) {
//        return categoryRepository.findByNameContainingIgnoreCase(name); // Припускаємо, що це метод репозиторію
//    }
//
//    // Оновлення категорії
//    public Category updateCategory(UUID id, Category category) {
//        Category existingCategory = categoryRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));
//        existingCategory.setName(category.getName());
//        existingCategory.setLogoUrl(category.getLogoUrl());
//        return categoryRepository.save(existingCategory);
//    }

    @Transactional
    public void deleteCategory(UUID id) {
        var category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));

        if (category.getLogoUrl() != null) {
            minioService.deleteImage(category.getLogoUrl(), PRODUCT);
        }

        repository.delete(category);
    }

    @Transactional(readOnly = true)
    public Page<CategoryDto> getPaginatedCategories(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var categoryPage = repository.findAll(pageable);

        return categoryPage.map(asm::toDto);
    }

    @Transactional(readOnly = true)
    public byte[] getCategoryLogo(UUID categoryId) {
        var logoPath = getCategoryById(categoryId).getLogoUrl();

        if (logoPath == null || logoPath.isEmpty()) {
            return new byte[0];
        }

        return minioService.downloadImage(logoPath, CATEGORY);
    }

    private Category getCategoryByIdInternal(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));
    }
}