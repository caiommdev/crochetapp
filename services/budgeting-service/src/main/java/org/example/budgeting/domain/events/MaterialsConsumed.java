package org.example.budgeting.domain.events;

import org.example.budgeting.domain.shared.DomainEvents;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Publicado quando um orçamento é concluído (DONE) — fecha o ciclo de vida da reserva,
 * registrando que os materiais reservados foram efetivamente consumidos.
 */
public record MaterialsConsumed(
        UUID budgetId,
        List<Line> lines,
        Instant occurredOn
) implements DomainEvents {

    public record Line(UUID materialId, Integer quantity, Integer meters) {}

    public MaterialsConsumed(UUID budgetId, List<Line> lines) {
        this(budgetId, lines, Instant.now());
    }
}
