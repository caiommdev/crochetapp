package org.example.catalog.infrastructure.client;

import java.util.UUID;

public record StockWriteDto(
        UUID materialId,
        Integer quantity,
        Integer meters
) {}
