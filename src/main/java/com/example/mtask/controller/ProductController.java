package com.example.mtask.controller;

import com.example.mtask.dto.ProductRcvDto;
import com.example.mtask.dto.ProductSendDto;
import com.example.mtask.service.ProductServiceImp;
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

    private final ProductServiceImp productServiceImp;

    @Autowired
    public ProductController(ProductServiceImp productServiceImp) {
        this.productServiceImp = productServiceImp;
    }

    @PostMapping
    public ResponseEntity<ProductSendDto> createProduct(@RequestParam String name,
                                                        @RequestParam UUID categoryId,
                                                        @RequestParam(required = false) MultipartFile logoFile) {
        var product = productServiceImp.createProduct(name, categoryId, logoFile);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductSendDto> getProduct(@PathVariable UUID id) {
        var product = productServiceImp.getProductDtoById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductSendDto> updateProduct(@PathVariable UUID id, @ModelAttribute ProductRcvDto updateRequest) {
        var updatedProduct = productServiceImp.updateProduct(id, updateRequest);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productServiceImp.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductSendDto>> searchProducts(@RequestParam String name) {
        var products = productServiceImp.searchProductsByName(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}/logo")
    public ResponseEntity<byte[]> getProductLogo(@PathVariable UUID id) {
        byte[] logo = productServiceImp.getProductLogo(id);

        if (logo == null || logo.length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(logo);
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<ProductSendDto>> getProductsByCategory(@PathVariable String categoryName) {
        var products = productServiceImp.getProductsByCategoryName(categoryName);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/unique-names")
    public ResponseEntity<List<String>> getProductsWithUniqueNames() {
        var uniqueProducts = productServiceImp.getProductsWithUniqueNames();
        return new ResponseEntity<>(uniqueProducts, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<ProductSendDto>> getPaginatedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var productPage = productServiceImp.getPaginatedProducts(page, size);
        return new ResponseEntity<>(productPage, HttpStatus.OK);
    }
}