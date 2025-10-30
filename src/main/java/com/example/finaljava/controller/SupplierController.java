package com.example.finaljava.controller;

import com.example.finaljava.model.Supplier;
import com.example.finaljava.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/supplier")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @GetMapping("")
    public List<Supplier> getAllSuppliers() {
        var suppliers = supplierService.getAllSuppliers();
        return suppliers;
    }

    @GetMapping("/{id}")
    public Supplier findSupplierById(@PathVariable int id) {
        var supplier = supplierService.findSupplierById(id);
        return supplier;
    }

    @PostMapping("")
    public ResponseEntity<?> createSupplier(@Valid @RequestBody Supplier supplier) {
        this.supplierService.createSupplier(supplier);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Supplier created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable int id, @Valid @RequestBody Supplier supplier) {
        var supplier1 = supplierService.updateSupplier(id,supplier);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Supplier updated successfully");
        return new ResponseEntity<>(supplier1, HttpStatus.OK);
    }

    @PostMapping("/searchSupplierName")
    public ResponseEntity<?> searchSupplierName(@RequestParam String name, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        var supplier = supplierService.searchSupplierName(name,page,size);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    @GetMapping("/paginated")
    public ResponseEntity<?> paginated(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        var supplier = supplierService.paginated(page,size);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable int id) {
        var supplier = supplierService.deleteById(id);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }
}
