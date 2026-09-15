package org.example.catalog.domain.events;

import org.example.catalog.domain.shared.DomainEvents;

import java.time.Instant;
import java.util.UUID;

public record RecipeDeleted(UUID recipeId, Instant occurredOn) implements DomainEvents {

    public RecipeDeleted(UUID recipeId) {
        this(recipeId, Instant.now());
    }
}
