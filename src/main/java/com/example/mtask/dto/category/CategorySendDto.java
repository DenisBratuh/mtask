package com.example.mtask.dto.category;

import com.example.mtask.dto.product.ProductSendDto;

import java.util.List;
import java.util.UUID;

public class CategorySendDto {

    private UUID id;
    private String name;

    private String logoUrl;
    private List<ProductSendDto> productSendDtoList;

    public CategorySendDto() {
    }

    public CategorySendDto(UUID id, String name, String logoUrl, List<ProductSendDto> productSendDtoList) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.productSendDtoList = productSendDtoList;
    }

    public UUID getId() {
        return id;
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

    public void setId(UUID id) {
        this.id = id;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public List<ProductSendDto> getProductDtoList() {
        return productSendDtoList;
    }
}
