package org.example.catalog.domain.events;

import org.example.catalog.domain.shared.DomainEvents;

import java.time.Instant;
import java.util.UUID;

public record MaterialDeleted(UUID materialId, Instant occurredOn) implements DomainEvents {

    public MaterialDeleted(UUID materialId) {
        this(materialId, Instant.now());
    }
}
