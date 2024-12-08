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

    public CategoryDto toDto(Category entity/*, List<ProductDto> productDtoList*/) {
        return new CategoryDto(
                entity.getId(),
                entity.getName(),
                entity.getLogoUrl(),
                productAsm.toDto(entity.getProducts())
//                productDtoList
        );
    }

//    public CategoryDto toCategoryDTO(Category category) {
//        List<ProductDto> productDTOList = category.getProducts().stream()
//                .map(product -> toDto(product))
//                .collect(Collectors.toList());
//
//        return new CategoryDto(category.getId(), category.getName(), category.getLogoUrl(), productDTOList);
//    }

    public Category toEntity(CategoryDto dto) {
        return new Category(
                dto.getId(),
                dto.getName(),
                dto.getLogoUrl()
        );
    }
}
