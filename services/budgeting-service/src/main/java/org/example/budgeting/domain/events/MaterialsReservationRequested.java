package org.example.budgeting.domain.events;

import org.example.budgeting.domain.shared.DomainEvents;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MaterialsReservationRequested(
        UUID budgetId,
        List<Line> lines,
        Instant occurredOn
) implements DomainEvents {

    public record Line(UUID materialId, Integer quantity, Integer meters) {}

    public MaterialsReservationRequested(UUID budgetId, List<Line> lines) {
        this(budgetId, lines, Instant.now());
    }
}
