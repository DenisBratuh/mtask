package com.example.mtask.assembler;

import com.example.mtask.dto.product.ProductSendDto;
import com.example.mtask.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        if (entityList == null) {
            return Collections.emptyList();
        }

        return entityList.stream()
                .filter(Objects::nonNull)
                .map(this::toDto)
                .toList();
    }
}
