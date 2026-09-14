package org.example.auth.infrastructure.persistence.mappers;

import org.example.auth.domain.models.User;
import org.example.auth.infrastructure.persistence.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .passwordHash(entity.getPasswordHash())
                .email(entity.getEmail())
                .build();
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;
        return UserEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .passwordHash(domain.getPasswordHash())
                .email(domain.getEmail())
                .build();
    }
}
