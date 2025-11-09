package com.example.finaljava.controller;

import com.example.finaljava.model.Role;
import com.example.finaljava.model.User;
import com.example.finaljava.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper; // for parsing JSON from FormData

    // ✅ Get all users
    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        var users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // ✅ Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        var user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    // ✅ Create new user (FormData compatible)
    @PostMapping("")
    public ResponseEntity<?> createUser(
            @RequestParam("name") String name,
            @RequestParam("gender") String gender,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("tel") String tel,
            @RequestParam("address") String address,
            @RequestParam("role") String roleJson, // comes as JSON string from FormData
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws Exception {
        // convert role JSON string to Role object
        Role role = objectMapper.readValue(roleJson, Role.class);

        // build user object
        User user = new User();
        user.setName(name);
        user.setGender(gender);
        user.setEmail(email);
        user.setPassword(password);
        user.setTel(tel);
        user.setAddress(address);
        user.setRole(role);

        var saved = userService.createUser(user, file);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User Created Successfully!");
        response.put("user", saved);
        return ResponseEntity.ok(response);
    }

    // ✅ Update user (FormData compatible)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable int id,
            @RequestParam("name") String name,
            @RequestParam("gender") String gender,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("tel") String tel,
            @RequestParam("address") String address,
            @RequestParam("role") String roleJson,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws Exception {
        Role role = objectMapper.readValue(roleJson, Role.class);

        User user = new User();
        user.setName(name);
        user.setGender(gender);
        user.setEmail(email);
        user.setPassword(password);
        user.setTel(tel);
        user.setAddress(address);
        user.setRole(role);

        var updated = userService.updateUser(id, user, file);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User Updated Successfully!");
        response.put("user", updated);
        return ResponseEntity.ok(response);
    }

    // ✅ Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) throws Exception {
        userService.deleteUser(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User Deleted Successfully!");
        return ResponseEntity.ok(response);
    }

    // ✅ Search by name
    @PostMapping("/searchByName")
    public ResponseEntity<List<User>> searchByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.searchByName(name, page, size));
    }

    // ✅ Pagination
    @GetMapping("/paginated")
    public ResponseEntity<List<User>> paginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.paginated(page, size));
    }
}
