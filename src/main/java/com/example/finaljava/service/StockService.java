package com.example.finaljava.service;

import com.example.finaljava.exceptions.MyResourceNotFoundException;
import com.example.finaljava.model.Product;
import com.example.finaljava.model.Stock;
import com.example.finaljava.repository.ProductRepository;
import com.example.finaljava.repository.StockRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    // ------------------- GET ALL -------------------
    public List<Stock> getStocks() {
        return stockRepository.findAll();
    }

    // ------------------- GET BY ID -------------------
    public Stock getStockById(int id) {
        return stockRepository.findStockById(id);
    }

    // ------------------- CREATE -------------------
    @Transactional
    public Stock createStock(@Valid Stock stock) {
        Product product = productRepository.findById(stock.getProduct().getId())
                .orElseThrow(() -> new MyResourceNotFoundException("Product not found with id: " + stock.getProduct().getId()));

        // Add quantity to product's stock
        int newStock = product.getStock() + stock.getQuantity();
        product.setStock(newStock);

        productRepository.save(product);
        return stockRepository.save(stock);
    }

    // ------------------- UPDATE -------------------
    @Transactional
    public Stock updateStock(int id, @Valid Stock updatedStock) {
        Stock existingStock = stockRepository.findStockById(id);
        if (existingStock == null) {
            throw new MyResourceNotFoundException("Stock not found with id: " + id);
        }

        Product product = productRepository.findById(existingStock.getProduct().getId())
                .orElseThrow(() -> new MyResourceNotFoundException("Product not found with id: " + existingStock.getProduct().getId()));

        // Adjust product stock: subtract old quantity, add new quantity
        int adjustedStock = product.getStock() - existingStock.getQuantity() + updatedStock.getQuantity();
        product.setStock(adjustedStock);
        productRepository.save(product);

        existingStock.setQuantity(updatedStock.getQuantity());
        existingStock.setPrice(updatedStock.getPrice());
        existingStock.setSupplier(updatedStock.getSupplier());

        return stockRepository.save(existingStock);
    }

    // ------------------- DELETE -------------------
    @Transactional
    public void deleteStock(int id) {
        Stock stock = stockRepository.findStockById(id);
        if (stock == null) {
            throw new MyResourceNotFoundException("Stock not found with id: " + id);
        }

        Product product = productRepository.findById(stock.getProduct().getId())
                .orElseThrow(() -> new MyResourceNotFoundException("Product not found with id: " + stock.getProduct().getId()));

        // Subtract quantity from product stock
        int updatedStock = product.getStock() - stock.getQuantity();
        product.setStock(Math.max(updatedStock, 0)); // prevent negative stock
        productRepository.save(product);

        stockRepository.delete(stock);
    }
}