package org.example.auth.api.controllers;

import org.example.auth.api.dtos.LoginRequest;
import org.example.auth.api.dtos.LoginResponse;
import org.example.auth.api.dtos.RegisterRequest;
import org.example.auth.api.dtos.UserResponse;
import org.example.auth.application.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        log.info("HTTP register recebido username={} email={}", request.username(), request.email());
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("HTTP login recebido identificador={}", request.username());
        return ResponseEntity.ok(authService.login(request));
    }
}
