package org.example.catalog.infrastructure.client;

import java.util.UUID;

/** Payload enviado ao Inventory Service para criar/atualizar o estoque de um material. */
public record StockWriteDto(
        UUID materialId,
        Integer quantity,
        Integer meters
) {}
