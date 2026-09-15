package org.example.budgeting.infrastructure.messaging.events;

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
) {
    public record PointItem(String name, Integer centimetersPerPoint, Integer quantity) {}

    public record RequirementItem(UUID materialId, Integer quantityNeeded) {}
}
