package org.example.inventory.infrastructure.messaging.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Cópia local do evento publicado pelo catalog-service — o inventory-service só usa
 * materialId/quantity/meters para manter o próprio estoque.
 */
public record MaterialDefined(
        UUID materialId,
        String name,
        String type,
        BigDecimal price,
        String color,
        Integer metersPerSkein,
        Integer quantity,
        Integer meters,
        Instant occurredOn
) {}
