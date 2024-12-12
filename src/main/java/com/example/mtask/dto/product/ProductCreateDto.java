package com.example.mtask.dto.product;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class ProductCreateDto {
    private String name;
    private UUID categoryId;
    private MultipartFile logoFile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public MultipartFile getLogoFile() {
        return logoFile;
    }

    public void setLogoFile(MultipartFile logoFile) {
        this.logoFile = logoFile;
    }

}