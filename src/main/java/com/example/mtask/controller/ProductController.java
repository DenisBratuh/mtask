package com.example.mtask.controller;

import com.example.mtask.dto.product.ProductCreateDto;
import com.example.mtask.dto.product.ProductSendDto;
import com.example.mtask.dto.product.ProductUpdateDto;
import com.example.mtask.service.imp.ProductServiceImp;
import com.example.mtask.service.inteface.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    @Autowired
    public ProductController(ProductServiceImp service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductSendDto> createProduct(@ModelAttribute ProductCreateDto dto) {
        var product = service.createProduct(dto);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductSendDto> getProduct(@PathVariable UUID id) {
        var product = service.getProductDtoById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EDITOR')")
    public ResponseEntity<ProductSendDto> updateProduct(@PathVariable UUID id, @ModelAttribute ProductUpdateDto updateRequest) {
        var updatedProduct = service.updateProduct(id, updateRequest);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        service.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductSendDto>> searchProducts(@RequestParam String name) {
        var products = service.searchProductsByName(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}/logo")
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
    public ResponseEntity<List<ProductSendDto>> getProductsByCategory(@PathVariable String categoryName) {
        var products = service.getProductsByCategoryName(categoryName);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/unique-names")
    public ResponseEntity<List<String>> getProductsWithUniqueNames() {
        var uniqueProducts = service.getProductsWithUniqueNames();
        return new ResponseEntity<>(uniqueProducts, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<ProductSendDto>> getPaginatedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var productPage = service.getPaginatedProducts(page, size);
        return new ResponseEntity<>(productPage, HttpStatus.OK);
    }
}