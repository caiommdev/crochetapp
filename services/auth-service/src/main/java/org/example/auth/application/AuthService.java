package org.example.auth.application;

import org.example.auth.api.dtos.LoginRequest;
import org.example.auth.api.dtos.LoginResponse;
import org.example.auth.api.dtos.RegisterRequest;
import org.example.auth.api.dtos.UserResponse;
import org.example.auth.domain.models.User;
import org.example.auth.domain.repository.UserRepository;
import org.example.auth.infrastructure.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request) {
        log.info("Recebido pedido de registro para username={} email={}", request.username(), request.email());
        
        if (userRepository.existsByUsername(request.username())) {
            log.warn("Falha no registro: username já está em uso username={}", request.username());
            throw new IllegalStateException("Username já está em uso: " + request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Falha no registro: e-mail já está em uso email={}", request.email());
            throw new IllegalStateException("E-mail já está em uso: " + request.email());
        }


        User user = User.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .email(request.email())
                .build();

        User savedUser = userRepository.save(user);
        log.info("Usuário registrado com sucesso userId={} username={}", savedUser.getId(), savedUser.getUsername());
        return toUserResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        log.info("Tentativa de login para identificador={}", request.username());
        User user = userRepository.findByUsernameOrEmail(request.username(), request.username())
                .orElseThrow(() -> {
                    log.warn("Falha no login: usuário não encontrado identificador={}", request.username());
                    return new IllegalArgumentException("Invalid username or email");
                });

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Falha no login: senha inválida userId={} username={}", user.getId(), user.getUsername());
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtService.generateToken(user);
        log.info("Login realizado com sucesso userId={} username={}", user.getId(), user.getUsername());
        return new LoginResponse(token, "Bearer");
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
