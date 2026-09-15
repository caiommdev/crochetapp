package org.example.catalog.domain.events;

import org.example.catalog.domain.shared.DomainEvents;

import java.time.Instant;
import java.util.UUID;

public record ProductDefined(
        UUID productId,
        String name,
        UUID recipeId,
        Instant occurredOn
) implements DomainEvents {

    public ProductDefined(UUID productId, String name, UUID recipeId) {
        this(productId, name, recipeId, Instant.now());
    }
}
