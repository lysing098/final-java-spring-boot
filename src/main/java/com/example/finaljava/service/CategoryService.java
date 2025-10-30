package com.example.finaljava.service;

import com.example.finaljava.exceptions.MyResourceNotFoundException;
import com.example.finaljava.model.Category;
import com.example.finaljava.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Async
    public Category findById(int id) {
        var category = categoryRepository.findCategoryById(id);
        if(category == null) {
            throw new MyResourceNotFoundException("Category with id " + id + " not found");
        }
        return category;
    }

    @Async
    @Transactional
    public Category createCategory(Category category) {
        var category1 = this.categoryRepository.save(category);
        return category1;

    }

    @Async
    @Transactional
    public Category updateById(int id, @Valid Category category) {
        var category1 = categoryRepository.findCategoryById(id);
        if(category1 == null) {
            throw new MyResourceNotFoundException("Category with id " + id + " not found");
        }
        category1.setName(category.getName());
        return category1; // return the entity with correct ID
    }

    public Category deleteById(int id) {
        var category = this.categoryRepository.findCategoryById(id);
        if(category == null) {
            throw new MyResourceNotFoundException("Category with id " + id + " not found");
        }
        this.categoryRepository.delete(category);
        return category;
    }

    @Async
    public List<Category> searchByName(String name, int page, int pageSize) {
        var pageable = PageRequest.of(page, pageSize);
        return categoryRepository.findByNameContainingIgnoreCase(name, pageable).getContent();
    }


    @Async
    public List<Category> paginated(int page, int pageSize) {
        var pageable = PageRequest.of(page, pageSize);
        return categoryRepository.findAll(pageable).getContent();
    }
}
