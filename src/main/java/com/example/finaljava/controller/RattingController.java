package com.example.finaljava.controller;

import com.example.finaljava.model.Ratting;
import com.example.finaljava.service.RattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratting")
@RequiredArgsConstructor
public class RattingController {

    private final RattingService rattingService;

    // ✅ Get all ratings
    @GetMapping
    public ResponseEntity<List<Ratting>> getAllRattings() {
        return ResponseEntity.ok(rattingService.getAllRattings());
    }

    // ✅ Get rating by ID
    @GetMapping("/{id}")
    public ResponseEntity<Ratting> getRattingById(@PathVariable int id) {
        Ratting ratting = rattingService.getRattingById(id);
        return ResponseEntity.ok(ratting);
    }

    // ✅ Create new rating
    @PostMapping
    public ResponseEntity<Ratting> createRatting(@RequestBody Ratting ratting) {
        Ratting saved = rattingService.createRatting(ratting);
        return ResponseEntity.ok(saved);
    }

    // ✅ Update existing rating
    @PutMapping("/{id}")
    public ResponseEntity<Ratting> updateRatting(@PathVariable int id, @RequestBody Ratting ratting) {
        Ratting updated = rattingService.updateRatting(id, ratting);
        return ResponseEntity.ok(updated);
    }

    // ✅ Delete rating
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRatting(@PathVariable int id) {
        rattingService.deleteRatting(id);
        return ResponseEntity.noContent().build();
    }
}
