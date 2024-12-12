package com.example.mtask.dto;

import java.util.List;
import java.util.UUID;

public class CategoryDto {

    private UUID id;
    private String name;
    //TODO check for update?
    private String logoUrl;
    private List<ProductSendDto> productSendDtoList;

    public CategoryDto() {

    }

    public CategoryDto(UUID id, String name, String logoUrl, List<ProductSendDto> productSendDtoList) {
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

    public void setProductDtoList(List<ProductSendDto> productSendDtoList) {
        this.productSendDtoList = productSendDtoList;
    }
}
