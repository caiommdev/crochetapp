package org.example.budgeting.application.dtos;

import java.util.List;
import java.util.UUID;

/**
 * Pedido de reserva/liberação enviado ao Inventory Service. Espelha o contrato do Inventory.
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
