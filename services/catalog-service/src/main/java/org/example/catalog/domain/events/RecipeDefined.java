package org.example.catalog.domain.events;

import org.example.catalog.domain.shared.DomainEvents;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RecipeDefined(
        UUID recipeId,
        String name,
        String description,
        List<PointItem> points,
        List<RequirementItem> materialRequirements,
        Instant occurredOn
) implements DomainEvents {

    public record PointItem(String name, Integer centimetersPerPoint, Integer quantity) {}

    public record RequirementItem(UUID materialId, Integer quantityNeeded) {}

    public RecipeDefined(UUID recipeId, String name, String description,
                          List<PointItem> points, List<RequirementItem> materialRequirements) {
        this(recipeId, name, description, points, materialRequirements, Instant.now());
    }
}
