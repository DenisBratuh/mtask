package com.example.mtask.dto.product;

import java.util.UUID;

public class ProductSendDto {
    private UUID id;
    private String name;

    private String logoUrl;

    private UUID categoryId;

    public ProductSendDto() {
    }

    public ProductSendDto(UUID id, String name, String logoUrl, UUID categoryId) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.categoryId = categoryId;
    }

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

    public UUID getCategoryId() {
        return categoryId;
    }
}
