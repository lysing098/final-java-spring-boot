package com.example.finaljava.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_title", columnNames = "title"),
        @UniqueConstraint(name = "uk_product_barcode", columnNames = "barcode")
})
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String title;

    @NotEmpty(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Discount must be greater than 0")
    @DecimalMax(value = "100.0", message = "Discount cannot exceed 100")
    private Double discount;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;

    @NotNull(message = "Barcode is required")
    @Positive(message = "Barcode must be a positive number")
    private Integer barcode;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    @NotNull(message = "Category is required!")
    @Size(min = 2, max = 30, message = "Category must be 2–30 characters")
    private String category;

    private String image; // stores filename only
}
