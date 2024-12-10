package com.example.mtask.mapper;

import com.example.mtask.dto.CategoryDto;
import com.example.mtask.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryAsm {

    private final ProductAsm productAsm;

    @Autowired
    public CategoryAsm(ProductAsm productAsm) {
        this.productAsm = productAsm;
    }

    public CategoryDto toDto(Category entity) {
        return new CategoryDto(
                entity.getId(),
                entity.getName(),
                entity.getLogoUrl(),
                productAsm.toDto(entity.getProducts())
        );
    }
}
