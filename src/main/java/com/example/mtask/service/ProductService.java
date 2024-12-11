package com.example.mtask.service;

import com.example.mtask.dto.ProductDto;
import com.example.mtask.entity.Product;
import com.example.mtask.mapper.ProductAsm;
import com.example.mtask.repository.ProductRepository;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static com.example.mtask.entity.LogoType.PRODUCT;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final MinioService minioService;
    private final ProductAsm productAsm;

    public ProductService(ProductRepository productRepository, CategoryService categoryService, MinioService minioService, ProductAsm productAsm) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.minioService = minioService;
        this.productAsm = productAsm;
    }

    public ProductDto createProduct(String name, UUID categoryId, MultipartFile logoFile) throws FileUploadException {
        var category = categoryService.getCategoryEntityById(categoryId);

        //TODO use var instead?
        String logoPath = null;
        if (logoFile != null && !logoFile.isEmpty()) {
            logoPath = minioService.uploadImage(logoFile, PRODUCT);
        }

        var product = new Product();
        product.setCategory(category);
        product.setName(name);
        product.setLogoUrl(logoPath);

        product = productRepository.save(product);
        return productAsm.toDto(product);
    }

    public ProductDto getProductDtoById(UUID id) {
        return productAsm.toDto(getProductById(id));
    }

    private Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public ProductDto updateProduct(UUID id, String name, UUID categoryId, MultipartFile logoFile) throws FileUploadException {
        var product = getProductById(id);

        if (name != null && !name.isEmpty()) {
            product.setName(name);
        }


        if (categoryId != null) {
            var category = categoryService.getCategoryEntityById(categoryId);
            product.setCategory(category);
        }

        if (logoFile != null && !logoFile.isEmpty()) {
            //TODO а як сам логотип видалити?
            var logoPath = minioService.uploadImage(logoFile, PRODUCT);
            if (product.getLogoUrl() != null) {
                minioService.deleteImage(product.getLogoUrl(), PRODUCT);
            }
            product.setLogoUrl(logoPath);
        }

        var savedEntity = productRepository.save(product);
        return productAsm.toDto(savedEntity);
    }

    public void deleteProduct(UUID id) {
        var product = getProductById(id);

        if (product.getLogoUrl() != null) {
            minioService.deleteImage(product.getLogoUrl(), PRODUCT);
        }

        productRepository.delete(product);
    }

    public byte[] getProductLogo(UUID productId) {
        var product = getProductById(productId);
        if (product.getLogoUrl() == null || product.getLogoUrl().isEmpty()) {
            return new byte[0];
        }

        return minioService.downloadImage(product.getLogoUrl(), PRODUCT);
    }

    public List<ProductDto> searchProductsByName(String name) {
        var entityList = productRepository.findByNameContainingIgnoreCase(name);
        return productAsm.toDto(entityList);
    }

//check
//    public List<ProductDto> getProductsByCategoryId(UUID categoryId) {
//        var list = productRepository.findByCategoryId(categoryId);
//        return productAsm.toDto(list);
//    }

    public List<ProductDto> getProductsByCategoryName(String name) {
        var list = productRepository.findByCategoryName(name);
        return productAsm.toDto(list);
    }

    public List<String> getProductsWithUniqueNames() {
        return productRepository.findDistinctProductNames();
    }

    public Page<ProductDto> getPaginatedProducts(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var productPage = productRepository.findAll(pageable);

        return productPage.map(productAsm::toDto);
    }
}