package com.example.mtask.dto;

import java.util.UUID;

public class ProductDto {
    private UUID id;
    private String name;
    //todo check
    private String logoUrl;
    //todo check
    private UUID categoryId;

    public ProductDto(UUID id, String name, String logoUrl, UUID categoryId) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.categoryId = categoryId;
    }

    // Гетери та сетери
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
}
