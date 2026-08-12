package org.example.inventory.api.dto;

import java.util.List;
import java.util.UUID;

/**
 * Pedido de reserva/liberação de estoque. O Budgeting Service calcula, a partir da receita,
 * quanto abater de cada material e envia as linhas prontas. O Inventory apenas aplica o delta.
 */
public record ReservationRequest(
        UUID budgetId,
        List<ReservationLine> lines
) {
    public record ReservationLine(
            UUID materialId,
            Integer quantity,
            Integer meters
    ) {}
}
