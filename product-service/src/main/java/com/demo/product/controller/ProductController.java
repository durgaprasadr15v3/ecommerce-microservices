package com.demo.product.controller;

import com.demo.product.dto.ProductDtos;
import com.demo.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000/")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ProductDtos.ProductResponse> create(
            @RequestPart("product") String productJson,
            @RequestPart("file") MultipartFile file) {

        ObjectMapper mapper = new ObjectMapper();
        ProductDtos.CreateProductRequest request;

        try {
            request = mapper.readValue(productJson, ProductDtos.CreateProductRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON", e);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request,file));
    }

    @GetMapping
    public ResponseEntity<List<ProductDtos.ProductResponse>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(productService.getAllProducts(category, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDtos.ProductResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDtos.ProductResponse> updateStock(
            @PathVariable String id,
            @RequestBody ProductDtos.UpdateStockRequest request) {
        return ResponseEntity.ok(productService.updateStock(id, request.getQuantity()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "product-service"));
    }

    @GetMapping("/welcome")
    public String welcome()
    {
        return "welcome to the product service";
    }

}
