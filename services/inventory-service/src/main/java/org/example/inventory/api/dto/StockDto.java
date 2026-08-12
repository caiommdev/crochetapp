package org.example.inventory.api.dto;

import java.util.UUID;

/**
 * Representa o estoque de um material para leitura/escrita via API.
 */
public record StockDto(
        UUID materialId,
        Integer quantity,
        Integer meters
) {}
