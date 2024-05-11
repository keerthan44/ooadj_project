package com.mycompany.myapp.web.rest.controller;

import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.domain.Motorbike;
import com.mycompany.myapp.domain.enumeration.BikeStatus;
import com.mycompany.myapp.service.MotorbikeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
public class MotorbikeController {

    @Autowired
    private MotorbikeService motorbikeService;

    @GetMapping("/motorbikes")
    public List<Motorbike> getAllMotorbikes() {
        return motorbikeService.findAll();
    }

    @GetMapping("/motorbikes/{id}")
    public ResponseEntity<Motorbike> getCustomer(@PathVariable Long id) {
        Motorbike motorbike = motorbikeService.findOne(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(motorbike);
    }

    @PutMapping("/motorbikes/{id}/status")
    public ResponseEntity<Motorbike> updateMotorbikeStatus(@PathVariable Long id, @RequestParam BikeStatus status) {
        Motorbike motorbike = motorbikeService.findOne(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        motorbike = motorbikeService.updateMotorbikeStatus(id, status);
        return ResponseEntity.ok(motorbike);
    }

    @PostMapping("/motorbikes")
    public ResponseEntity<Motorbike> createMotorbike(@RequestParam String make, @RequestParam String model) {
        Motorbike motorbike = motorbikeService.createAndSaveMotorbike(make, model);
        return ResponseEntity.ok(motorbike);
    }
}
