package com.example.finaljava.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "supplier",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_supplier_name",columnNames = "name"),
                @UniqueConstraint(name = "uk_supplier_phone",columnNames = "phone")
        }
)
@Getter
@Setter
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false,unique = true)
    @NotEmpty(message = "Supplier is required!")
    @Size(min = 2,max = 30,message = "Supplier name must be 2-30 characters!")
    private String name;
    private String address;
    // 🔹 Telephone validation (Cambodia phone number example)
    @Pattern(
            regexp = "^(\\+855|0)([1-9][0-9]{7,8})$",
            message = "Invalid phone number format! Example: +85512345678 or 012345678"
    )
    @NotBlank(message = "Telephone number is required!")
    private String phone;

}
