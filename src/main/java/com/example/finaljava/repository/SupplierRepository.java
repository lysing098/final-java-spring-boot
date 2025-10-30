package com.example.finaljava.repository;

import com.example.finaljava.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Supplier findSupplierById(int id);

    Page<Supplier> findByNameContainingIgnoreCase(String name, PageRequest pageable);
}
