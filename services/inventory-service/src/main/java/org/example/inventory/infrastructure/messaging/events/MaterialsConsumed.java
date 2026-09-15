package org.example.inventory.infrastructure.messaging.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Cópia local do evento publicado pelo budgeting-service ao concluir um orçamento. */
public record MaterialsConsumed(
        UUID budgetId,
        List<Line> lines,
        Instant occurredOn
) {
    public record Line(UUID materialId, Integer quantity, Integer meters) {}
}
