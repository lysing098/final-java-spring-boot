package com.example.finaljava.controller;

import com.example.finaljava.model.Customer;
import com.example.finaljava.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // ------------------- CREATE -------------------
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createCustomer(
            @RequestParam String name,
            @RequestParam String gender,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String tel,
            @RequestParam String address,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws Exception {

        Customer customer = new Customer();
        customer.setName(name);
        customer.setGender(gender);
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setTel(tel);
        customer.setAddress(address);

        Customer savedCustomer = customerService.createCustomer(customer, file);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer Created");
        response.put("customer", savedCustomer);
        return ResponseEntity.ok(response);
    }

    // ------------------- UPDATE -------------------
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateCustomer(
            @PathVariable int id,
            @RequestParam String name,
            @RequestParam String gender,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String tel,
            @RequestParam String address,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws Exception {

        Customer customer = new Customer();
        customer.setName(name);
        customer.setGender(gender);
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setTel(tel);
        customer.setAddress(address);

        Customer updatedCustomer = customerService.updateCustomer(id, customer, file);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer Updated");
        response.put("customer", updatedCustomer);
        return ResponseEntity.ok(response);
    }

    // ------------------- GET ALL -------------------
    @GetMapping("")
    public ResponseEntity<?> getAllCustomers() {
        var customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    // ------------------- GET BY ID -------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable int id) {
        var customer = customerService.findCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    // ------------------- DELETE -------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable int id) throws Exception {
        customerService.deleteCustomer(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer Deleted");
        return ResponseEntity.ok(response);
    }
}
