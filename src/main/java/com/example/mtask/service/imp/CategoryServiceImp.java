package com.example.mtask.service.imp;

import com.example.mtask.assembler.CategoryAsm;
import com.example.mtask.dto.category.CategoryRcvDto;
import com.example.mtask.dto.category.CategorySendDto;
import com.example.mtask.entity.Category;
import com.example.mtask.repository.CategoryRepository;
import com.example.mtask.service.inteface.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.mtask.enums.LogoType.CATEGORY;

@Service
public class CategoryServiceImp implements CategoryService {

    private final CategoryAsm asm;
    private final CategoryRepository repository;
    private final MinioService minioService;

    @Autowired
    public CategoryServiceImp(CategoryRepository repository, CategoryAsm asm, MinioService minioService) {
        this.repository = repository;
        this.asm = asm;
        this.minioService = minioService;
    }

    @Transactional
    @Override
    public CategorySendDto createCategory(CategoryRcvDto dto) {
        var logoFile = dto.getFile();
        var name = dto.getName();

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
    @Override
    public CategorySendDto getCategoryById(UUID id) {
        var foundEntity = getCategoryByIdInternal(id);
        return asm.toDto(foundEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public Category getCategoryEntityById(UUID id) {
        return getCategoryByIdInternal(id);
    }

    @Transactional
    @Override
    public void deleteCategory(UUID id) {
        var category = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));

        if (category.getLogoUrl() != null) {
            minioService.deleteImage(category.getLogoUrl());
        }

        repository.delete(category);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CategorySendDto> getPaginatedCategories(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var categoryPage = repository.findAll(pageable);

        return categoryPage.map(asm::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] getCategoryLogo(UUID categoryId) {
        var logoPath = getCategoryByIdInternal(categoryId).getLogoUrl();

        if (logoPath == null || logoPath.isEmpty()) {
            return new byte[0];
        }

        return minioService.downloadImage(logoPath);
    }

    private Category getCategoryByIdInternal(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));
    }
}