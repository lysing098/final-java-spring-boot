package com.example.finaljava.controller;

import com.example.finaljava.model.Stock;
import com.example.finaljava.service.StockService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("")
    public List<Stock> getStocks() {
        return stockService.getStocks();
    }

    @GetMapping("/{id}")
    public Stock getStockById(@PathVariable int id) {
        return stockService.getStockById(id);
    }

    @PostMapping("")
    public Stock createStock(@Valid @RequestBody Stock stock) {
        return stockService.createStock(stock);
    }

    @PutMapping("/{id}")
    public Stock updateStock(@PathVariable int id, @Valid @RequestBody Stock stock) {
        return stockService.updateStock(id, stock);
    }

    @DeleteMapping("/{id}")
    public String deleteStock(@PathVariable int id) {
        stockService.deleteStock(id);
        return "Stock deleted successfully";
    }
}
