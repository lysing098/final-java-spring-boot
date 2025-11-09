package com.example.finaljava.service;

import com.example.finaljava.exceptions.MyResourceNotFoundException;
import com.example.finaljava.model.Product;
import com.example.finaljava.repository.ProductRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.address}")
    private String localhost;

    @Value("${server.port}")
    private String port;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Helper: get full URL for images
    private String getImageUrl(String filename) {
        if (filename == null || filename.isEmpty()) return "";
        return "http://" + localhost + ":" + port + "/uploads/" + filename;
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        products.forEach(p -> p.setImage(getImageUrl(p.getImage())));
        return products;
    }

    public Product getProductById(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Product not found with id " + id));
        product.setImage(getImageUrl(product.getImage()));
        return product;
    }

    @Transactional
    public void createProduct(@Valid Product product, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());
            product.setImage(fileName);
        }
        productRepository.save(product);
    }

    @Transactional
    public void updateProductById(int id, @Valid Product product, MultipartFile file) throws IOException {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Product not found with id: " + id));

        // Update basic fields
        existing.setTitle(product.getTitle());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setDiscount(product.getDiscount());
        existing.setBarcode(product.getBarcode());
        existing.setStock(product.getStock());
        existing.setCategory(product.getCategory());

        // Handle image
        if (file != null && !file.isEmpty()) {
            if (existing.getImage() != null && !existing.getImage().isEmpty()) {
                Path oldFile = Paths.get(uploadDir).resolve(existing.getImage());
                Files.deleteIfExists(oldFile);
            }
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());
            existing.setImage(fileName);
        }

        productRepository.save(existing);
    }

    @Transactional
    public void deleteProductById(int id) throws IOException {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Product not found with id " + id));

        if (existing.getImage() != null && !existing.getImage().isEmpty()) {
            Path filePath = Paths.get(uploadDir).resolve(existing.getImage());
            Files.deleteIfExists(filePath);
        }

        productRepository.delete(existing);
    }
}
