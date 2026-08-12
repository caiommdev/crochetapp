package org.example.catalog.infrastructure.client;

import java.util.UUID;

/** Espelho do estoque retornado pelo Inventory Service. */
public record StockView(
        UUID materialId,
        Integer quantity,
        Integer meters
) {}
