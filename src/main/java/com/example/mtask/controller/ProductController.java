package com.example.mtask.controller;

import com.example.mtask.dto.ProductDto;
import com.example.mtask.service.ProductService;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestParam String name,
                                                    @RequestParam UUID categoryId,
                                                    @RequestParam(required = false) MultipartFile logoFile) throws FileUploadException {
        var product = productService.createProduct(name, categoryId, logoFile);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable UUID id) {
        var product = productService.getProductDtoById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable UUID id,
                                                    @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) UUID categoryId,
                                                    @RequestParam(required = false) MultipartFile logoFile) throws FileUploadException {
        var updatedProduct = productService.updateProduct(id, name, categoryId, logoFile);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String name) {
        var products = productService.searchProductsByName(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}/logo")
    public ResponseEntity<byte[]> getProductLogo(@PathVariable UUID id) {
        byte[] logo = productService.getProductLogo(id);

        if (logo == null || logo.length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(logo);
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable String categoryName) {
        var products = productService.getProductsByCategoryName(categoryName);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/unique-names")
    public ResponseEntity<List<String>> getProductsWithUniqueNames() {
        var uniqueProducts = productService.getProductsWithUniqueNames();
        return new ResponseEntity<>(uniqueProducts, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getPaginatedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var productPage = productService.getPaginatedProducts(page, size);
        return new ResponseEntity<>(productPage, HttpStatus.OK);
    }
}