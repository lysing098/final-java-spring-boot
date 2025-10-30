package com.example.finaljava.controller;

import com.example.finaljava.model.Order;
import com.example.finaljava.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("")
    public List<Order> getOrders() {
        var orders = orderService.getOrders();
        return orders;
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable int id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("")
    public Order addOrder(@RequestBody Order order) {
        return  orderService.createOrder(order);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable int id, @RequestBody Order order) {
        return orderService.updateOrder(id,order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable int id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully");
    }


}
