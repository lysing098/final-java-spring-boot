package com.example.finaljava.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "user",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_name", columnNames = "name"),
                @UniqueConstraint(name = "uk_user_email", columnNames = "email")
        }
)
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 🔹 Username validation
    @Column(nullable = false, unique = true, length = 15)
    @NotEmpty(message = "User name is required!")
    @Size(min = 2, max = 15, message = "User name must be 2–15 characters!")
    private String name;

    // 🔹 Gender validation (optional)
    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;

    // 🔹 Email validation (unique)
    @Email(message = "Invalid email format!")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email must be valid, e.g., example@gmail.com"
    )
    @NotBlank(message = "Email is required!")
    @Column(nullable = false, unique = true)
    private String email;


    // 🔹 Password validation
    @NotBlank(message = "Password is required!")
    @Size(min = 6, max = 50, message = "Password must be between 6–50 characters!")
    private String password;

    // 🔹 Telephone validation (Cambodia phone number example)
    @Pattern(
            regexp = "^(\\+855|0)([1-9][0-9]{7,8})$",
            message = "Invalid phone number format! Example: +85512345678 or 012345678"
    )
    @NotBlank(message = "Telephone number is required!")

    private String tel;

    // 🔹 Address validation
    @Size(min = 2, max = 100, message = "Address must be 2–100 characters!")
    private String address;

    private String photo;

    @ManyToOne()
    @NotNull(message = "Role is required!")
    private Role role;
}
