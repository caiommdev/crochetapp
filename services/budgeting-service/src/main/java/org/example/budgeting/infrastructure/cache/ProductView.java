package org.example.budgeting.infrastructure.cache;

import java.util.UUID;

public record ProductView(
        UUID id,
        String name,
        RecipeView recipe,
        Object image
) {}
