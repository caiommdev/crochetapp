package org.example.budgeting.infrastructure.cache;

import java.math.BigDecimal;
import java.util.UUID;

public record MaterialView(
        UUID id,
        String name,
        MaterialType type,
        BigDecimal price,
        Object image,
        String color,
        Integer quantity,
        Integer meters
) {}
