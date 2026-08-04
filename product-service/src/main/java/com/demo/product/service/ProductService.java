package com.demo.product.service;

import com.demo.product.dto.ProductDtos;
import com.demo.product.model.Product;
import com.demo.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageUploadService imageUploadService;

    public ProductDtos.ProductResponse createProduct(ProductDtos.CreateProductRequest request, MultipartFile file) {


                String imageUrl =imageUploadService.uploadImage(file);
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .imageUrl(imageUrl)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponse(productRepository.save(product));
    }

    public List<ProductDtos.ProductResponse> getAllProducts(String category, String search) {
        List<Product> products;
        if (search != null && !search.isBlank()) {
            products = productRepository.findByNameContainingIgnoreCaseAndActiveTrue(search);
        } else if (category != null && !category.isBlank()) {
            products = productRepository.findByCategoryAndActiveTrue(category);
        } else {
            products = productRepository.findByActiveTrue();
        }
        return products.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ProductDtos.ProductResponse getById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        return mapToResponse(product);
    }

    public ProductDtos.ProductResponse updateStock(String id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));

        int newStock = product.getStock() + quantity;
        if (newStock < 0) throw new RuntimeException("Insufficient stock for product: " + id);

        product.setStock(newStock);
        product.setUpdatedAt(LocalDateTime.now());
        log.info("Updated stock for product {}: {} -> {}", id, product.getStock(), newStock);
        return mapToResponse(productRepository.save(product));
    }

    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        product.setActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    private ProductDtos.ProductResponse mapToResponse(Product p) {
        ProductDtos.ProductResponse r = new ProductDtos.ProductResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setCategory(p.getCategory());
        r.setPrice(p.getPrice());
        r.setStock(p.getStock());
        r.setImageUrl(p.getImageUrl());
        r.setActive(p.getActive());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}
