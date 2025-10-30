package com.example.finaljava.controller;

import com.example.finaljava.model.Product;
import com.example.finaljava.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("")
    public ResponseEntity<?> getAllProducts() {
        var products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        var product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping("")
    public ResponseEntity<?> createProduct(@Valid Product product, @RequestParam(value = "file",required = false) MultipartFile file) throws Exception {
        productService.createProduct(product,file);
        Map<String,Object> response = new HashMap<>();
        response.put("message","Product created");
        response.put("data",product);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProductById(@PathVariable int id, @Valid Product product ,@RequestParam(value = "file",required = false) MultipartFile file) throws Exception  {
        productService.updateProductById(id,product,file);

        var updatedProduct = productService.getProductById(id);

        Map<String,Object> response = new HashMap<>();
        response.put("message","Product updated");
        response.put("data",updatedProduct);
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable int id,@RequestParam(value = "file",required = false) MultipartFile file) throws Exception {
        productService.deleteProductById(id);
        Map<String,Object> response = new HashMap<>();
        response.put("message","Product deleted");
        return ResponseEntity.ok(response);

    }

    @PostMapping("/searchByname")
    public ResponseEntity<?> searchProductByName(@RequestParam String name,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        var product  =this.productService.findByName(name,page,size);
        return ResponseEntity.ok(product);

    }

    @GetMapping("/paginated")
    public ResponseEntity<?> paginated(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        var products = this.productService.paginated(page,size);
        return ResponseEntity.ok(products);
    }

}
