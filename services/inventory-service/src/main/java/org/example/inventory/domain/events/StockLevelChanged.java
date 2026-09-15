package org.example.inventory.domain.events;

import org.example.inventory.domain.shared.DomainEvents;

import java.time.Instant;
import java.util.UUID;

public record StockLevelChanged(
        UUID materialId,
        Integer quantity,
        Integer meters,
        Instant occurredOn
) implements DomainEvents {

    public StockLevelChanged(UUID materialId, Integer quantity, Integer meters) {
        this(materialId, quantity, meters, Instant.now());
    }
}
