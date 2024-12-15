package com.example.mtask.assembler;

import com.example.mtask.dto.category.CategorySendDto;
import com.example.mtask.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A utility class responsible for transforming {@link Category} entities into {@link CategorySendDto} data transfer objects.
 * <p>
 * This class uses the {@link ProductAsm} to convert the list of products associated with a category.
 * </p>
 */
@Component
public class CategoryAsm {

    private final ProductAsm productAsm;

    @Autowired
    public CategoryAsm(ProductAsm productAsm) {
        this.productAsm = productAsm;
    }

    public CategorySendDto toDto(Category entity) {
        return new CategorySendDto(
                entity.getId(),
                entity.getName(),
                entity.getLogoUrl(),
                productAsm.toDto(entity.getProducts())
        );
    }
}
