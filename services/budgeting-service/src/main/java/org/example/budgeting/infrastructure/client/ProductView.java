package org.example.budgeting.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductView(
        UUID id,
        String name,
        RecipeView recipe,
        Object image
) {}
