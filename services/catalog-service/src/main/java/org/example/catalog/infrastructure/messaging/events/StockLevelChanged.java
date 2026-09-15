package org.example.catalog.infrastructure.messaging.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Cópia local do evento publicado pelo inventory-service — o catalog-service não depende
 * da classe de domínio do inventory, só do formato (contrato) do evento.
 */
public record StockLevelChanged(UUID materialId, Integer quantity, Integer meters, Instant occurredOn) {}
