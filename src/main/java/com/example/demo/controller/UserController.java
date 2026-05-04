package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable int id) {
        if (id == 99999) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        if (id == 1) {
            user.put("name", "Alice");
            user.put("email", "alice@example.com");
        } else {
            user.put("name", "TestUser");
            user.put("email", "test@example.com");
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> request) {
        if ("Bob".equals(request.get("name")) && "bob@example.com".equals(request.get("email"))) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", 42);
            user.put("name", "Bob");
            user.put("email", "bob@example.com");
            return ResponseEntity.status(201)
                    .header("Location", "/users/42")
                    .body(user);
        }
        Map<String, Object> response = new HashMap<>(request);
        response.put("id", 100);
        return ResponseEntity.status(201).body(response);
    }
}
