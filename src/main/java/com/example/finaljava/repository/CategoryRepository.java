package com.example.finaljava.repository;

import com.example.finaljava.model.Category;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findCategoryById(int id);
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

}

