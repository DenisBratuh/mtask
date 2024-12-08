package com.example.mtask.service;

import com.example.mtask.dto.ProductDto;
import com.example.mtask.entity.Product;
import com.example.mtask.mapper.ProductAsm;
import com.example.mtask.repository.CategoryRepository;
import com.example.mtask.repository.ProductRepository;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static com.example.mtask.entity.LogoType.PRODUCT;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    //TODO probably change it for service
    private final CategoryRepository categoryRepository;
    private final MinioService minioService;
    private final ProductAsm productAsm;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, MinioService minioService, ProductAsm productAsm) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.minioService = minioService;
        this.productAsm = productAsm;
    }

    public ProductDto createProduct(String name, UUID categoryId, MultipartFile logoFile) throws FileUploadException {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        //TODO use var instead?
        String logoPath = null;
        if (logoFile != null && !logoFile.isEmpty()) {
            logoPath = minioService.uploadLogo(logoFile, PRODUCT);
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
        Product product = getProductById(id);

        //TODO check uniq
        if (name != null && !name.isEmpty()) {
            product.setName(name);
        }


        if (categoryId != null) {
            var category = categoryRepository.findById(categoryId);
            category.ifPresent(product::setCategory);
        }

        if (logoFile != null && !logoFile.isEmpty()) {
            // Якщо логотип вже існує, видаляємо його з MinIO
            //TODO краще спочатку спробувати зберегти, і якщо все окей, то видалити
            if (product.getLogoUrl() != null) {
                minioService.deleteLogo(product.getLogoUrl(), PRODUCT);
            }
            String logoPath = minioService.uploadLogo(logoFile, PRODUCT); // Завантаження нового логотипу
            product.setLogoUrl(logoPath); // Оновлюємо шлях до нового логотипу
        }

        var savedEntity = productRepository.save(product);
        return productAsm.toDto(savedEntity);
    }

    public void deleteProduct(UUID id) {
        Product product = getProductById(id);

        if (product.getLogoUrl() != null) {
            minioService.deleteLogo(product.getLogoUrl(), PRODUCT);
        }

        productRepository.delete(product);
    }

    public byte[] getProductLogo(UUID productId) {
        var product = getProductById(productId);
        if (product.getLogoUrl() == null || product.getLogoUrl().isEmpty()) {
            return new byte[0];
        }

        return minioService.downloadLogo(product.getLogoUrl(), PRODUCT);
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
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        return productPage.map(productAsm::toDto);
    }
}