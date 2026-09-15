package org.example.inventory.infrastructure.messaging.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Cópia local do evento publicado pelo budgeting-service ao cancelar um orçamento. */
public record MaterialsReleaseRequested(
        UUID budgetId,
        List<Line> lines,
        Instant occurredOn
) {
    public record Line(UUID materialId, Integer quantity, Integer meters) {}
}
