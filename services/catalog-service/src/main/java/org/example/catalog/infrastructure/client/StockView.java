package org.example.catalog.infrastructure.client;

import java.util.UUID;

public record StockView(
        UUID materialId,
        Integer quantity,
        Integer meters
) {}
