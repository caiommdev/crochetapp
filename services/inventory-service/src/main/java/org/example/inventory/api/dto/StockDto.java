package org.example.inventory.api.dto;

import java.util.UUID;

public record StockDto(
        UUID materialId,
        Integer quantity,
        Integer meters
) {}
