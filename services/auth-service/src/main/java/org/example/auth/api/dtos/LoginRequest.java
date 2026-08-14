package org.example.auth.api.dtos;

public record LoginRequest(
    String username,
    String password
) {
}
