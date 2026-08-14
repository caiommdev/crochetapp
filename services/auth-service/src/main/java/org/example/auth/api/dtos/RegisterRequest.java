package org.example.auth.api.dtos;

public record RegisterRequest(
    String username,
    String password,
    String email
) {
}
