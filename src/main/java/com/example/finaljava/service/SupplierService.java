package com.example.finaljava.service;

import com.example.finaljava.exceptions.MyResourceNotFoundException;
import com.example.finaljava.model.Supplier;
import com.example.finaljava.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {
    @Autowired
    private SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Async
    public Supplier findSupplierById(int id) {
        var supplier =  supplierRepository.findSupplierById(id);
        if (supplier == null) {
            throw new MyResourceNotFoundException("Supplier with id " + id + " not found");
        }
        return supplier;
    }

    @Async
    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Async
    @Transactional
    public Supplier updateSupplier(int id, @Valid Supplier supplier) {
        Supplier existingSupplier = supplierRepository.findSupplierById(id);
        if (existingSupplier == null) {
            throw new MyResourceNotFoundException("Supplier with id " + id + " not found");
        }

        existingSupplier.setName(supplier.getName());
        existingSupplier.setAddress(supplier.getAddress());
        existingSupplier.setPhone(supplier.getPhone());

        try {
            return supplierRepository.save(existingSupplier);
        } catch (Exception e) {
            // Optional: catch unique constraint violations and throw friendly message
            throw new RuntimeException("Failed to update supplier. Name or phone may already exist.", e);
        }
    }

    @Async
    public List<Supplier> searchSupplierName(String name, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return supplierRepository.findByNameContainingIgnoreCase(name,pageable).getContent();
    }

    @Async
    public List<Supplier> paginated(int page, int size) {
        var pageable = PageRequest.of(page, size);
        return supplierRepository.findAll(pageable).getContent();
    }

    public Supplier deleteById(int id) {
        var supplier = supplierRepository.findSupplierById(id);
        if (supplier == null) {
            throw new MyResourceNotFoundException("Supplier with id " + id + " not found");
        }
        supplierRepository.delete(supplier);
        return supplier;
    }
}
