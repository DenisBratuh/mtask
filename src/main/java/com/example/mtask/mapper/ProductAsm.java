package com.example.mtask.mapper;

import com.example.mtask.dto.ProductDto;
import com.example.mtask.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProductAsm {

    public ProductDto toDto(Product entity) {
        return new ProductDto(
                entity.getId(),
                entity.getName(),
                entity.getLogoUrl(),
                entity.getCategory().getId()
        );
    }
    public List<ProductDto> toDto(List<Product> entityList) {
        //todo?
        if (entityList == null) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::toDto).toList();
    }

    //TODO
    public Product toEntity(ProductDto dto) {

        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setLogoUrl(dto.getLogoUrl());
        //product.setCategory(category);
        //TODO search for Category in service? Circular dep?
        return product;
    }
}
