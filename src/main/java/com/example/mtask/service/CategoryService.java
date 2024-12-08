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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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

    public CategoryDto createCategory(String name, MultipartFile logoFile) throws FileUploadException {
        String logoUrl = null;

        if (logoFile != null && !logoFile.isEmpty()) {
            logoUrl = minioService.uploadLogo(logoFile, CATEGORY);
        }

        var category = new Category();
        category.setName(name);
        category.setLogoUrl(logoUrl);

        category = repository.save(category);
        return asm.toDto(category);
    }

    public CategoryDto getCategoryById(UUID id) {
        var foundEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));

        return asm.toDto(foundEntity);
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


    public void deleteCategory(UUID id) {
        var category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));

        if (category.getLogoUrl() != null) {
            minioService.deleteLogo(category.getLogoUrl(), PRODUCT);
        }

        repository.delete(category);
    }

    public Page<CategoryDto> getPaginatedCategories(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var categoryPage = repository.findAll(pageable);

        return categoryPage.map(asm::toDto);
    }

    public byte[] getCategoryLogo(UUID categoryId) {
        var logoPath = getCategoryById(categoryId).getLogoUrl();

        if (logoPath == null || logoPath.isEmpty()) {
            return new byte[0];
        }

        return minioService.downloadLogo(logoPath, CATEGORY);
    }
}