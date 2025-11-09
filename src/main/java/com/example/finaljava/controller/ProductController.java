package com.example.finaljava.controller;

import com.example.finaljava.model.Product;
import com.example.finaljava.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/product")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createProduct(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam Double discount,
            @RequestParam Integer barcode,
            @RequestParam int stock,
            @RequestParam String category,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setDiscount(discount);
        product.setBarcode(barcode);
        product.setStock(stock);
        product.setCategory(category);

        productService.createProduct(product, image);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product created successfully");
        response.put("data", productService.getProductById(product.getId()));
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateProduct(
            @PathVariable int id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam Double discount,
            @RequestParam Integer barcode,
            @RequestParam int stock,
            @RequestParam String category,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setDiscount(discount);
        product.setBarcode(barcode);
        product.setStock(stock);
        product.setCategory(category);

        productService.updateProductById(id, product, image);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product updated successfully");
        response.put("data", productService.getProductById(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) throws IOException {
        productService.deleteProductById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product deleted successfully");
        return ResponseEntity.ok(response);
    }
}
