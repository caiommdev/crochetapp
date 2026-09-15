package org.example.catalog.domain.events;

import org.example.catalog.domain.shared.DomainEvents;

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
) implements DomainEvents {

    public MaterialDefined(UUID materialId, String name, String type, BigDecimal price,
                            String color, Integer metersPerSkein, Integer quantity, Integer meters) {
        this(materialId, name, type, price, color, metersPerSkein, quantity, meters, Instant.now());
    }
}
