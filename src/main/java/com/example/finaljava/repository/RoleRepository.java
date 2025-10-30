package com.example.finaljava.repository;

import com.example.finaljava.model.Role;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findRoleById(Integer id);

    List<Role> findByNameContainingIgnoreCase(@Valid String name, PageRequest pageble);
}
