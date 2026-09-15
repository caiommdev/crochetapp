package org.example.catalog.domain.events;

import org.example.catalog.domain.shared.DomainEvents;

import java.time.Instant;
import java.util.UUID;

public record ProductDeleted(UUID productId, Instant occurredOn) implements DomainEvents {

    public ProductDeleted(UUID productId) {
        this(productId, Instant.now());
    }
}
