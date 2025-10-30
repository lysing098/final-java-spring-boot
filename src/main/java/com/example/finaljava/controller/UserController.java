package com.example.finaljava.controller;

import com.example.finaljava.model.User;
import com.example.finaljava.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        var users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        var user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("")
    public ResponseEntity<?> createUser(@Valid User user
            , @RequestParam(value = "file",required = false)MultipartFile file) throws Exception {
        userService.createUser(user,file);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User Created");
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id,@Valid User user,
                                        @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        userService.updateUser(id,user,file);

        var updatedUser = userService.findUserById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User Updated");
        response.put("user", updatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id,@RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        userService.deleteUser(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User Deleted");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/searchByName")
    public ResponseEntity<List<User>> searchByName(@RequestParam String name,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.searchByName(name, page, size));
    }

    @GetMapping("/paginated")
    public ResponseEntity<List<User>> paginated(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.paginated(page, size));
    }
}
