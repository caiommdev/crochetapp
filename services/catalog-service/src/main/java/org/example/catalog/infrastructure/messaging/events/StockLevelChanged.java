package org.example.catalog.infrastructure.messaging.events;

import java.time.Instant;
import java.util.UUID;

public record StockLevelChanged(UUID materialId, Integer quantity, Integer meters, Instant occurredOn) {}
