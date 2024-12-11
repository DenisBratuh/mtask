package com.example.mtask.mapper;

import com.example.mtask.dto.ProductSendDto;
import com.example.mtask.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProductAsm {

    public ProductSendDto toDto(Product entity) {
        return new ProductSendDto(
                entity.getId(),
                entity.getName(),
                entity.getLogoUrl(),
                entity.getCategory().getId()
        );
    }
    public List<ProductSendDto> toDto(List<Product> entityList) {
        //todo?
        if (entityList == null) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::toDto).toList();
    }
}
