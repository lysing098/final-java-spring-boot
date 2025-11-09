package com.example.finaljava.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ratting")
@Getter
@Setter
public class Ratting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private int id;

    private double rate;
    private int count;

//    // ✅ Correct ManyToOne side
//    @OneToOne(mappedBy = "rating")
//    @JsonBackReference // ✅ tells Jackson to ignore this when serializing
//    private Product product;



}
