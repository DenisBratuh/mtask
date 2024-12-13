package com.example.mtask.controller;

import com.example.mtask.dto.product.ProductCreateDto;
import com.example.mtask.dto.product.ProductSendDto;
import com.example.mtask.dto.product.ProductUpdateDto;
import com.example.mtask.service.imp.ProductServiceImp;
import com.example.mtask.service.inteface.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Product API", description = "API for managing products")
@RestController
@RequestMapping("/api/products")
@SecurityRequirement(name = "basicAuth")
public class ProductController {

    private final ProductService service;

    @Autowired
    public ProductController(ProductServiceImp service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new product", description = "Creates a new product and optionally uploads a logo.")
    @ApiResponse(responseCode = "201", description = "Product created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductSendDto.class)))
    public ResponseEntity<ProductSendDto> createProduct(@ModelAttribute ProductCreateDto dto) {
        var product = service.createProduct(dto);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves product details by ID.")
    @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductSendDto.class)))
    public ResponseEntity<ProductSendDto> getProduct(@PathVariable UUID id) {
        var product = service.getProductDtoById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('EDITOR')")
    @Operation(summary = "Update product", description = "Updates an existing product. Requires the 'EDITOR' role.")
    @ApiResponse(responseCode = "200", description = "Product updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductSendDto.class)))
    public ResponseEntity<ProductSendDto> updateProduct(@PathVariable UUID id, @ModelAttribute ProductUpdateDto updateRequest) {
        var updatedProduct = service.updateProduct(id, updateRequest);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Deletes a product by ID.")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        service.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name", description = "Searches for products with names containing the specified string.")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductSendDto.class)))
    public ResponseEntity<List<ProductSendDto>> searchProducts(@RequestParam String name) {
        var products = service.searchProductsByName(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}/logo")
    @Operation(summary = "Get product logo by ID", description = "Retrieves the logo of a product by ID.")
    @ApiResponse(responseCode = "200", description = "Logo retrieved successfully",
            content = @Content(mediaType = "image/jpeg"))
    public ResponseEntity<byte[]> getProductLogo(@PathVariable UUID id) {
        byte[] logo = service.getProductLogo(id);

        if (logo == null || logo.length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(logo);
    }

    @GetMapping("/category/{categoryName}")
    @Operation(summary = "Get products by category name", description = "Retrieves all products in a specific category.")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductSendDto.class)))
    public ResponseEntity<List<ProductSendDto>> getProductsByCategory(@PathVariable String categoryName) {
        var products = service.getProductsByCategoryName(categoryName);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/unique-names")
    @Operation(summary = "Get unique product names", description = "Retrieves a list of unique product names.")
    @ApiResponse(responseCode = "200", description = "Unique product names retrieved successfully",
            content = @Content(mediaType = "application/json"))
    public ResponseEntity<List<String>> getProductsWithUniqueNames() {
        var uniqueProducts = service.getProductsWithUniqueNames();
        return new ResponseEntity<>(uniqueProducts, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get paginated products", description = "Retrieves a paginated list of products.")
    @ApiResponse(responseCode = "200", description = "Paginated products retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    public ResponseEntity<Page<ProductSendDto>> getPaginatedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var productPage = service.getPaginatedProducts(page, size);
        return new ResponseEntity<>(productPage, HttpStatus.OK);
    }
}