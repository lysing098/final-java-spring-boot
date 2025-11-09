package com.example.finaljava.repository;


import com.example.finaljava.model.Ratting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RattingRepository extends JpaRepository<Ratting, Integer> {
}
