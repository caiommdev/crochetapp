package org.example.auth.api.dtos;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String email
) {
    
}
