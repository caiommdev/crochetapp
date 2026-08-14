package org.example.catalog.api.dto;

import org.example.catalog.domain.valueobjects.Image;

import java.util.UUID;

public record SaveProductRequest(
        String name,
        RecipeRef recipe,
        Image image
) {
    public record RecipeRef(UUID id) {}
}
