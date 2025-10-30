package com.example.finaljava.repository;

import com.example.finaljava.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.net.ContentHandler;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserById(int id);


    Page<User> findByNameContainingIgnoreCase( String name,PageRequest pageable);
}
