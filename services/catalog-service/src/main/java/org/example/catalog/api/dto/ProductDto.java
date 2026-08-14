package org.example.catalog.api.dto;

import org.example.catalog.domain.valueobjects.Image;

import java.util.UUID;

public record ProductDto(
        UUID id,
        String name,
        RecipeDto recipe,
        Image image
) {}
