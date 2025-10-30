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
public class CustomerController {
    @Autowired
    private CustomerService customerService;

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

    // ------------------- CREATE -------------------
    @PostMapping("")
    public ResponseEntity<?> createCustomer(@Valid Customer customer,
                                            @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        customerService.createCustomer(customer, file);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer Created");
        response.put("customer", customer);
        return ResponseEntity.ok(response);
    }

    // ------------------- UPDATE -------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable int id,
                                            @Valid Customer customer,
                                            @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        customerService.updateCustomer(id, customer, file);
        var updatedCustomer = customerService.findCustomerById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer Updated");
        response.put("customer", updatedCustomer);
        return ResponseEntity.ok(response);
    }

    // ------------------- DELETE -------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable int id) throws Exception {
        customerService.deleteCustomer(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer Deleted");
        return ResponseEntity.ok(response);
    }

    // ------------------- SEARCH BY NAME -------------------
    @PostMapping("/searchByName")
    public ResponseEntity<?> searchByName(@RequestParam String name,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(customerService.searchByName(name, page, size));
    }

    // ------------------- PAGINATED -------------------
    @GetMapping("/paginated")
    public ResponseEntity<?> paginated(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(customerService.paginated(page, size));
    }
}
