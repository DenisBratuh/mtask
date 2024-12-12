package com.example.mtask.service.inteface;

import com.example.mtask.dto.product.ProductCreateDto;
import com.example.mtask.dto.product.ProductUpdateDto;
import com.example.mtask.dto.product.ProductSendDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Interface for managing products in the application.
 * <p>
 * This interface defines methods for creating, updating, retrieving, searching, 
 * and deleting products, as well as handling their associated logos.
 * </p>
 */
public interface ProductService {

    //TODO update
    /**
     * Creates a new product with the specified details.
     *
     * @return a {@link ProductSendDto} containing the details of the created product
     */
    ProductSendDto createProduct(ProductCreateDto dto);

    /**
     * Retrieves the details of a product by its ID.
     *
     * @param id the UUID of the product
     * @return a {@link ProductSendDto} containing the product's details
     * @throws RuntimeException if the product with the specified ID is not found
     */
    ProductSendDto getProductDtoById(UUID id);

    //TODO update
    /**
     * Updates an existing product with new details.
     *
     * @param id          the UUID of the product to update
     * @return a {@link ProductSendDto} containing the updated product's details
     */
    ProductSendDto updateProduct(UUID id, ProductUpdateDto updateDto);

    /**
     * Deletes a product by its ID.
     *
     * @param id the UUID of the product to delete
     * @throws RuntimeException if the product with the specified ID is not found
     */
    void deleteProduct(UUID id);

    /**
     * Retrieves the logo of a product by its ID.
     *
     * @param productId the UUID of the product
     * @return a byte array containing the content of the logo
     * @throws RuntimeException if the product or its logo is not found
     */
    byte[] getProductLogo(UUID productId);

    /**
     * Searches for products with names that contain the specified string (case-insensitive).
     *
     * @param name the string to search for in product names
     * @return a list of {@link ProductSendDto} matching the search criteria
     */
    List<ProductSendDto> searchProductsByName(String name);

    /**
     * Retrieves all products associated with a specific category name.
     *
     * @param name the name of the category
     * @return a list of {@link ProductSendDto} in the specified category
     */
    List<ProductSendDto> getProductsByCategoryName(String name);

    /**
     * Retrieves a list of unique product names.
     *
     * @return a list of strings representing the unique product names
     */
    List<String> getProductsWithUniqueNames();

    /**
     * Retrieves a paginated list of products.
     *
     * @param page the page number to retrieve (zero-based index)
     * @param size the number of products per page
     * @return a {@link Page} of {@link ProductSendDto} containing the paginated products
     */
    Page<ProductSendDto> getPaginatedProducts(int page, int size);
}