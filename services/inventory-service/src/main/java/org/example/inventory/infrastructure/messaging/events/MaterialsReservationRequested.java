package org.example.inventory.infrastructure.messaging.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Cópia local do evento publicado pelo budgeting-service ao aceitar um orçamento. */
public record MaterialsReservationRequested(
        UUID budgetId,
        List<Line> lines,
        Instant occurredOn
) {
    public record Line(UUID materialId, Integer quantity, Integer meters) {}
}
