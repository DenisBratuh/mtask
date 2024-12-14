package com.example.mtask.service.inteface;

import com.example.mtask.dto.category.CategoryRcvDto;
import com.example.mtask.dto.category.CategorySendDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

/**
 * Interface for managing categories in the application.
 * <p>
 * This interface defines methods for creating, updating, retrieving, deleting categories,
 * and managing their associated logos.
 * </p>
 */
public interface CategoryService {

    //TODO
    /**
     * Creates a new category with the specified details.
     *
     * @return a {@link CategorySendDto} containing the details of the created category
     */
    CategorySendDto createCategory(CategoryRcvDto dto);

    //TODO THROWS!!!
    /**
     * Retrieves the details of a category by its ID.
     *
     * @param id the UUID of the category
     * @return a {@link CategorySendDto} containing the category's details
     * @throws RuntimeException if the category with the specified ID is not found
     */
    CategorySendDto getCategoryById(UUID id);

    /**
     * Retrieves the entity of a category by its ID.
     *
     * @param id the UUID of the category
     * @return the {@link com.example.mtask.entity.Category} entity
     * @throws RuntimeException if the category with the specified ID is not found
     */
    com.example.mtask.entity.Category getCategoryEntityById(UUID id);

    /**
     * Deletes a category by its ID.
     *
     * @param id the UUID of the category to delete
     * @throws RuntimeException if the category with the specified ID is not found
     */
    void deleteCategory(UUID id);

    /**
     * Retrieves a paginated list of categories.
     *
     * @param page the page number to retrieve (zero-based index)
     * @param size the number of categories per page
     * @return a {@link Page} of {@link CategorySendDto} containing the paginated categories
     */
    Page<CategorySendDto> getPaginatedCategories(int page, int size);

    /**
     * Retrieves the logo of a category by its ID.
     *
     * @param categoryId the UUID of the category
     * @return a byte array containing the content of the logo
     * @throws RuntimeException if the category or its logo is not found
     */
    byte[] getCategoryLogo(UUID categoryId);
}