package com.example.finaljava.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(
        name = "category",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_name", columnNames = "name")
        }
)
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Category is required!")
    @Column(nullable = false)
    @Size(min = 2, max = 30,message = "Category name must be 2-30 characters")
    private String name;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Product> products;
}
