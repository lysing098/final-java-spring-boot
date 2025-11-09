package com.example.finaljava.repository;

import com.example.finaljava.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.net.ContentHandler;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findProductById(int id);

    Page<Product> findByTitleContainingIgnoreCase(String name, Pageable pageable);
}
