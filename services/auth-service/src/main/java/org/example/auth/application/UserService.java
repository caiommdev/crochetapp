package org.example.auth.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.auth.api.dtos.UpdateUserRequest;
import org.example.auth.api.dtos.UserResponse;
import org.example.auth.domain.models.User;
import org.example.auth.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toUserResponse).toList();
    }

    public Optional<UserResponse> findById(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toUserResponse);
    }

    public Optional<UserResponse> update(UUID userId, UpdateUserRequest request) {
        log.info("Atualizando usuário userId={}", userId);
        return userRepository.findById(userId).map(user -> {
            if (!user.getUsername().equals(request.username())
                    && userRepository.existsByUsername(request.username())) {
                log.warn("Falha ao atualizar usuário {}: username já está em uso username={}", userId, request.username());
                throw new IllegalStateException("Username já está em uso: " + request.username());
            }
            if (!user.getEmail().equals(request.email())
                    && userRepository.existsByEmail(request.email())) {
                log.warn("Falha ao atualizar usuário {}: e-mail já está em uso email={}", userId, request.email());
                throw new IllegalStateException("E-mail já está em uso: " + request.email());
            }

            user.setUsername(request.username());
            user.setEmail(request.email());
            UserResponse saved = toUserResponse(userRepository.save(user));
            log.info("Usuário atualizado com sucesso userId={} username={}", saved.id(), saved.username());
            return saved;
        });
    }

    public void deleteById(UUID userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("Falha ao remover usuário inexistente userId={}", userId);
            throw new IllegalStateException("Usuário não encontrado com ID: " + userId);
        }
        userRepository.deleteById(userId);
        log.info("Usuário removido com sucesso userId={}", userId);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
