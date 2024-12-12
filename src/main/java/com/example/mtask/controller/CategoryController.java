package com.example.mtask.controller;

import com.example.mtask.dto.category.CategoryRcvDto;
import com.example.mtask.dto.category.CategorySendDto;
import com.example.mtask.service.inteface.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@SecurityRequirement(name = "basicAuth")
public class CategoryController {

    private final CategoryService service;

    @Autowired
    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new category", description = "Creates a category and optionally uploads a logo.")
    @ApiResponse(responseCode = "201", description = "Category created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategorySendDto.class)))
    public ResponseEntity<CategorySendDto> createCategory(@ModelAttribute CategoryRcvDto dto) {
        var createdCategoryDto = service.createCategory(dto);
        return new ResponseEntity<>(createdCategoryDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID", description = "Retrieves the details of a category by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategorySendDto.class))),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategorySendDto> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getCategoryById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Deletes a category by its ID.")
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        service.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @Operation(summary = "Get paginated categories", description = "Retrieves a paginated list of categories.")
    @ApiResponse(responseCode = "200", description = "Paginated categories retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    public ResponseEntity<Page<CategorySendDto>> getPaginatedCategories(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        var categoryPage = service.getPaginatedCategories(page, size);
        return new ResponseEntity<>(categoryPage, HttpStatus.OK);
    }

    @GetMapping("/{id}/logo")
    @Operation(summary = "Get a category's logo", description = "Retrieves the logo of a category by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logo retrieved successfully", content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE)),
            @ApiResponse(responseCode = "204", description = "No logo found for the category")
    })
    public ResponseEntity<byte[]> getCategoryLogo(@PathVariable UUID id) {
        byte[] logo = service.getCategoryLogo(id);

        if (logo == null || logo.length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(logo);
    }
}