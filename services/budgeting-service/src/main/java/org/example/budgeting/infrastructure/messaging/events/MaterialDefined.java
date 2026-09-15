package org.example.budgeting.infrastructure.messaging.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MaterialDefined(
        UUID materialId,
        String name,
        String type,
        BigDecimal price,
        String color,
        Integer metersPerSkein,
        Integer quantity,
        Integer meters,
        Instant occurredOn
) {}
