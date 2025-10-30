package com.example.finaljava.service;

import com.example.finaljava.exceptions.MyResourceNotFoundException;
import com.example.finaljava.model.Role;
import com.example.finaljava.repository.RoleRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Async
    public Role getRoleById(Integer id) {
        var role = roleRepository.findRoleById(id);
        if (role == null) {
            throw new MyResourceNotFoundException("Role with id " + id + " not found");
        }
        return role;
    }

    @Async
    @Transactional
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Async
    @Transactional
    public Role updateById(int id, @Valid Role role) {
        var roleToUpdate = roleRepository.findRoleById(id);
        if (roleToUpdate == null) {
            throw new MyResourceNotFoundException("Role with id " + id + " not found");
        }

        roleToUpdate.setName(role.getName());
        return roleRepository.save(roleToUpdate); // ✅ must save
    }


    @Async
    public List<Role> searchByName(@Valid String name, int page, int pageSize) {
        var pageble = PageRequest.of(page, pageSize);
        var role = roleRepository.findByNameContainingIgnoreCase(name,pageble);
        if (role.isEmpty()) {
            throw new MyResourceNotFoundException("Role with name " + name + " not found");
        }
        return role;
    }

    @Async
    public List<Role> paginated(int page, int pageSize) {
        var pageble = PageRequest.of(page, pageSize);
        return roleRepository.findAll(pageble).getContent();
    }

    public Role deleteById(Integer id) {
        var role = roleRepository.findRoleById(id);
        if (role == null) {
            throw new MyResourceNotFoundException("Role with id " + id + " not found");
        }
        roleRepository.delete(role);
        return role;
    }
}
