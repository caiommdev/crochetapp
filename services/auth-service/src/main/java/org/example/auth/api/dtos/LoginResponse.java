package org.example.auth.api.dtos;

public record LoginResponse(
    String token,
    String tokenType
) {
    
}
