package org.example.inventory.api.dto;

import java.util.List;
import java.util.UUID;

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
