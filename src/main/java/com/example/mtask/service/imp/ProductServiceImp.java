package com.example.mtask.service.imp;

import com.example.mtask.dto.product.ProductCreateDto;
import com.example.mtask.dto.product.ProductUpdateDto;
import com.example.mtask.dto.product.ProductSendDto;
import com.example.mtask.entity.Product;
import com.example.mtask.mapper.ProductAsm;
import com.example.mtask.repository.ProductRepository;
import com.example.mtask.service.inteface.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static com.example.mtask.entity.LogoType.PRODUCT;

@Service
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryServiceImp categoryServiceImp;
    private final MinioService minioService;
    private final ProductAsm productAsm;

    public ProductServiceImp(ProductRepository productRepository, CategoryServiceImp categoryServiceImp, MinioService minioService, ProductAsm productAsm) {
        this.productRepository = productRepository;
        this.categoryServiceImp = categoryServiceImp;
        this.minioService = minioService;
        this.productAsm = productAsm;
    }

    @Override
    @Transactional
    public ProductSendDto createProduct(ProductCreateDto dto) {
        var categoryId = dto.getCategoryId();
        var logoFile = dto.getLogoFile();
        var name = dto.getName();

        var category = categoryServiceImp.getCategoryEntityById(categoryId);

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

    @Override
    @Transactional(readOnly = true)
    public ProductSendDto getProductDtoById(UUID id) {
        return productAsm.toDto(getProductById(id));
    }

    private Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    @Transactional
    public ProductSendDto updateProduct(UUID id, ProductUpdateDto updateDto) {
        var product = getProductById(id);

        updateNameIfPresent(product, updateDto.getName());
        updateCategoryIfPresent(product, updateDto.getCategoryId());
        updateLogo(product, updateDto.getLogoFile(), updateDto.isClearLogo());

        var savedEntity = productRepository.save(product);
        return productAsm.toDto(savedEntity);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        var product = getProductById(id);

        if (product.getLogoUrl() != null) {
            minioService.deleteImage(product.getLogoUrl(), PRODUCT);
        }

        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getProductLogo(UUID productId) {
        var product = getProductById(productId);
        if (product.getLogoUrl() == null || product.getLogoUrl().isEmpty()) {
            return new byte[0];
        }

        return minioService.downloadImage(product.getLogoUrl(), PRODUCT);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSendDto> searchProductsByName(String name) {
        var entityList = productRepository.findByNameContainingIgnoreCase(name);
        return productAsm.toDto(entityList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSendDto> getProductsByCategoryName(String name) {
        var list = productRepository.findByCategoryName(name);
        return productAsm.toDto(list);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getProductsWithUniqueNames() {
        return productRepository.findDistinctProductNames();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSendDto> getPaginatedProducts(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var productPage = productRepository.findAll(pageable);

        return productPage.map(productAsm::toDto);
    }

    private void updateNameIfPresent(Product product, String name) {
        if (name != null && !name.isEmpty()) {
            product.setName(name);
        }
    }

    private void updateCategoryIfPresent(Product product, UUID categoryId) {
        if (categoryId != null) {
            var category = categoryServiceImp.getCategoryEntityById(categoryId);
            product.setCategory(category);
        }
    }

    private void updateLogo(Product product, MultipartFile logoFile, boolean clearLogo) {
        if (clearLogo) {
            deleteLogoIfPresent(product);
        } else if (logoFile != null && !logoFile.isEmpty()) {
            replaceLogo(product, logoFile);
        }
    }

    private void deleteLogoIfPresent(Product product) {
        if (product.getLogoUrl() != null) {
            minioService.deleteImage(product.getLogoUrl(), PRODUCT);
            product.setLogoUrl(null);
        }
    }

    private void replaceLogo(Product product, MultipartFile logoFile) {
        var logoPath = minioService.uploadImage(logoFile, PRODUCT);
        deleteLogoIfPresent(product);
        product.setLogoUrl(logoPath);
    }
}