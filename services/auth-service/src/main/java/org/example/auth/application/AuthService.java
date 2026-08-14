package org.example.auth.application;

import lombok.RequiredArgsConstructor;
import org.example.auth.api.dtos.LoginRequest;
import org.example.auth.api.dtos.LoginResponse;
import org.example.auth.api.dtos.RegisterRequest;
import org.example.auth.api.dtos.UserResponse;
import org.example.auth.domain.models.User;
import org.example.auth.infrastructure.repositories.UserRepository;
import org.example.auth.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request) {
        
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalStateException("Username já está em uso: " + request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("E-mail já está em uso: " + request.email());
        }


        User user = User.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .email(request.email())
                .build();

        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameOrEmail(request.username(), request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or email"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, "Bearer");
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
