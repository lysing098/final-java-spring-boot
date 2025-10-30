package com.example.finaljava.controller;

import com.example.finaljava.model.Role;
import com.example.finaljava.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("")
    public ResponseEntity<?> getAllRoles() {
        var roles = roleService.getAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Integer id) {
        var role = roleService.getRoleById(id);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> createRole(@Valid @RequestBody Role role) {
        this.roleService.createRole(role);
        Map<String,String> response = new HashMap<>();
        response.put("message", "Role created successfully");
        return new ResponseEntity<>(role, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable int id, @Valid @RequestBody Role role) {
        Role role1 = roleService.updateById(id,role);
        return new ResponseEntity<>(role1, HttpStatus.OK);
    }

    @PostMapping("/searchByName")
    public ResponseEntity<?> searchByName(@Valid @RequestParam String name,@RequestParam(defaultValue = "0")int page,@RequestParam(defaultValue = "10")int pageSize) {
        var roles = roleService.searchByName(name,page,pageSize);
        return new ResponseEntity<>(roles, HttpStatus.OK);

    }

    @GetMapping("/paginated")
    public ResponseEntity<?> paginated(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int pageSize) {
        var roles = roleService.paginated(page,pageSize);
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
        var role = roleService.deleteById(id);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

}
