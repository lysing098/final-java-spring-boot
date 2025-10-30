package com.example.finaljava.service;

import com.example.finaljava.configuration.FileUploadUtil;
import com.example.finaljava.exceptions.MyResourceNotFoundException;
import com.example.finaljava.model.Product;
import com.example.finaljava.repository.ProductRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        var products = productRepository.findAll();

        for(Product product : products) {
            if(product.getPhoto()==null || product.getPhoto().isEmpty()) {
                product.setPhoto("");
            }
            else {
                product.setPhoto(localhost+":"+port+"/uploads/"+product.getPhoto());
            }
        }
        return products;
    }

    public Product getProductById(int id) {
        var product = productRepository.findProductById(id);
        if(product==null) {
            throw new MyResourceNotFoundException("product not found with id "+id);
        }

        if(product.getPhoto()==null || product.getPhoto().isEmpty()) {
            product.setPhoto("");
        }
        else {
            product.setPhoto(localhost+":"+port+"/uploads/"+product.getPhoto());
        }
        return  product;
    }

    @Async
    @Transactional
    public void createProduct(@Valid Product product, MultipartFile file) throws Exception {
        if(file!=null && !file.isEmpty()) {
            String fileName = UUID.randomUUID()+ "_" + file.getOriginalFilename();
            FileUploadUtil.saveFile(uploadDir, fileName, file);
            product.setPhoto(fileName);
        }
        else{
            product.setPhoto(null);
        }
        productRepository.save(product);
    }

    public void deleteProductById(int id) throws IOException {
        var productExist = productRepository.findProductById(id);
        if(productExist==null) {
            throw new MyResourceNotFoundException("product not found with id "+id);
        }
        if(productExist.getPhoto()!=null &&  !productExist.getPhoto().isEmpty()) {
            FileUploadUtil.removePhoto(uploadDir,productExist.getPhoto());
        }

        this.productRepository.deleteById(id);
    }

    public List<Product> findByName(String name, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return productRepository.findByNameContainingIgnoreCase(name,pageable).getContent();

    }

    public List<Product> paginated(int page, int size) {
        var pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable).getContent();
    }

    @Async
    @Transactional
    public void updateProductById(int id, @Valid Product product, MultipartFile file) throws IOException {
        var productExist = productRepository.findProductById(id);
        if (productExist == null) {
            throw new MyResourceNotFoundException("Product not found with id: " + id);
        }

        // ✅ Update basic info
        productExist.setName(product.getName());
        productExist.setDescription(product.getDescription());
        productExist.setPrice(product.getPrice());
        productExist.setBarcode(product.getBarcode());
        productExist.setStock(product.getStock());
        productExist.setCategory(product.getCategory());

        // ✅ Handle file upload safely
        if (file != null && !file.isEmpty()) {
            if (productExist.getPhoto() != null && !productExist.getPhoto().isEmpty()) {
                FileUploadUtil.removePhoto(uploadDir, productExist.getPhoto());
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            FileUploadUtil.saveFile(uploadDir, fileName, file);
            productExist.setPhoto(fileName);
        }

        // ✅ Save updated product
        productRepository.save(productExist);
    }

}
