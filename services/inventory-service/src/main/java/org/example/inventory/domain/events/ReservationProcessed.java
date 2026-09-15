package org.example.inventory.domain.events;

import org.example.inventory.domain.shared.DomainEvents;

import java.time.Instant;
import java.util.UUID;

public record ReservationProcessed(
        UUID budgetId,
        boolean success,
        String reason,
        Instant occurredOn
) implements DomainEvents {

    public ReservationProcessed(UUID budgetId, boolean success, String reason) {
        this(budgetId, success, reason, Instant.now());
    }
}
