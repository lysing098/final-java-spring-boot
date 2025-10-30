package com.example.finaljava.service;

import com.example.finaljava.exceptions.MyResourceNotFoundException;
import com.example.finaljava.model.Order;
import com.example.finaljava.model.OrderDetail;
import com.example.finaljava.model.Product;
import com.example.finaljava.repository.OrderRepository;
import com.example.finaljava.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    // ----------------- Get All Orders -----------------
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    // ----------------- Get Order by ID -----------------
    public Order getOrderById(int id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Order not found with id " + id));
    }

    // ----------------- Create Order -----------------
    public Order createOrder(Order order) {
        if (order.getOrderDetails() != null) {
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (OrderDetail detail : order.getOrderDetails()) {
                detail.setOrder(order);

                // Load product from DB
                Product product = productRepository.findById(detail.getProduct().getId())
                        .orElseThrow(() -> new MyResourceNotFoundException(
                                "Product not found with id " + detail.getProduct().getId()));

                // Check stock
                if (product.getStock() < detail.getQuantity()) {
                    throw new RuntimeException("Not enough stock for product: " + product.getName());
                }

                // Deduct stock
                product.setStock(product.getStock() - detail.getQuantity());
                productRepository.save(product);

                // Set the full product object
                detail.setProduct(product);

                // Calculate price
                BigDecimal detailPrice = BigDecimal.valueOf(product.getPrice())
                        .multiply(BigDecimal.valueOf(detail.getQuantity()));
                detail.setPrice(detailPrice.doubleValue());

                totalAmount = totalAmount.add(detailPrice);
            }

            order.setTotalAmount(totalAmount);
        }

        return orderRepository.save(order);
    }

    // ----------------- Update Order -----------------
    public Order updateOrder(int id, Order updatedOrder) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Order not found with id " + id));

        existingOrder.setSaleDate(updatedOrder.getSaleDate());
        existingOrder.setCustomer(updatedOrder.getCustomer());

        // Restore stock for old order details
        if (existingOrder.getOrderDetails() != null) {
            for (OrderDetail oldDetail : existingOrder.getOrderDetails()) {
                Product oldProduct = productRepository.findById(oldDetail.getProduct().getId())
                        .orElseThrow(() -> new MyResourceNotFoundException(
                                "Product not found with id " + oldDetail.getProduct().getId()));
                oldProduct.setStock(oldProduct.getStock() + oldDetail.getQuantity());
                productRepository.save(oldProduct);
            }
        }

        // Clear old details
        existingOrder.getOrderDetails().clear();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Add updated details and deduct stock
        if (updatedOrder.getOrderDetails() != null) {
            for (OrderDetail detail : updatedOrder.getOrderDetails()) {
                detail.setOrder(existingOrder);

                Product product = productRepository.findById(detail.getProduct().getId())
                        .orElseThrow(() -> new MyResourceNotFoundException(
                                "Product not found with id " + detail.getProduct().getId()));

                if (product.getStock() < detail.getQuantity()) {
                    throw new RuntimeException("Not enough stock for product: " + product.getName());
                }

                product.setStock(product.getStock() - detail.getQuantity());
                productRepository.save(product);

                detail.setProduct(product);

                BigDecimal detailPrice = BigDecimal.valueOf(product.getPrice())
                        .multiply(BigDecimal.valueOf(detail.getQuantity()));
                detail.setPrice(detailPrice.doubleValue());

                existingOrder.getOrderDetails().add(detail);
                totalAmount = totalAmount.add(detailPrice);
            }
        }

        existingOrder.setTotalAmount(totalAmount);

        return orderRepository.save(existingOrder);
    }

    // ----------------- Delete Order -----------------
    public void deleteOrder(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Order not found with id " + id));

        // Restore stock for order details
        if (order.getOrderDetails() != null) {
            for (OrderDetail detail : order.getOrderDetails()) {
                Product product = productRepository.findById(detail.getProduct().getId())
                        .orElseThrow(() -> new MyResourceNotFoundException(
                                "Product not found with id " + detail.getProduct().getId()));
                product.setStock(product.getStock() + detail.getQuantity());
                productRepository.save(product);
            }
        }

        // Delete order (cascade deletes order details)
        orderRepository.delete(order);
    }
}
