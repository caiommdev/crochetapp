package org.example.auth.api.dtos;

public record UpdateUserRequest (
    String username, 
    String email
) {
    
}
