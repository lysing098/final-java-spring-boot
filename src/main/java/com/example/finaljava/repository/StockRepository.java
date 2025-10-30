package com.example.finaljava.repository;

import com.example.finaljava.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    Stock findStockById(int id);
}
