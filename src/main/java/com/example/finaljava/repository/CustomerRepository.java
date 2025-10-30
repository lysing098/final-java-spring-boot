package com.example.finaljava.repository;

import com.example.finaljava.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Customer findCustomerById(int id);
    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
