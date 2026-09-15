package org.example.budgeting.infrastructure.messaging.events;

import java.time.Instant;
import java.util.UUID;

public record ProductDefined(
        UUID productId,
        String name,
        UUID recipeId,
        Instant occurredOn
) {}
