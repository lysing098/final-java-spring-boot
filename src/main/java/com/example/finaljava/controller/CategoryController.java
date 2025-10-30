package com.example.finaljava.controller;

import com.example.finaljava.model.Category;
import com.example.finaljava.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    private ResponseEntity<?> getAllCategories() {
        var categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    private ResponseEntity<?> findById(@PathVariable int id) {
        var category = categoryService.findById(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PostMapping("")
    private ResponseEntity<?> createCategory(@Valid @RequestBody Category category) {
        this.categoryService.createCategory(category);
        Map<String,String> response = new HashMap<>();
        response.put("message", "Category created");
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    private ResponseEntity<?> updateById(@PathVariable int id, @Valid @RequestBody Category category) {
        Category updatedCategory = this.categoryService.updateById(id, category);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<?> deleteById(@PathVariable int id) {
        Category deleteCategory = this.categoryService.deleteById(id);
        return new ResponseEntity<>(deleteCategory, HttpStatus.OK);
    }

    @PostMapping("/searchByName")
    private ResponseEntity<?> searchByName(@Valid @RequestParam String name ,@RequestParam(defaultValue = "0")int page ,@RequestParam(defaultValue = "10")int pageSize) {
        var categories = categoryService.searchByName(name,page,pageSize);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/paginated")
    public ResponseEntity<?> paginated(@RequestParam(defaultValue = "0")int page,@RequestParam(defaultValue = "10") int pageSize) {
        var categories = this.categoryService.paginated(page,pageSize);
        return new ResponseEntity<>(categories, HttpStatus.OK);

    }

}
