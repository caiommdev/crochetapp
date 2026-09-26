package org.example.auth.api.controllers;

import java.util.List;
import java.util.UUID;

import org.example.auth.api.dtos.UpdateUserRequest;
import org.example.auth.api.dtos.UserResponse;
import org.example.auth.application.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        log.info("HTTP list users recebido");
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
        log.info("HTTP get user recebido userId={}", id);
        return userService.findById(id).map(ResponseEntity::ok).orElseGet(() -> {
            log.warn("HTTP get user retornando 404 userId={}", id);
            return ResponseEntity.notFound().build();
        });
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id, @RequestBody UpdateUserRequest req) {
        log.info("HTTP update user recebido userId={} username={} email={}", id, req.username(), req.email());
        return userService.update(id, req).map(ResponseEntity::ok).orElseGet(() -> {
            log.warn("HTTP update user retornando 404 userId={}", id);
            return ResponseEntity.notFound().build();
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("HTTP delete user recebido userId={}", id);
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
