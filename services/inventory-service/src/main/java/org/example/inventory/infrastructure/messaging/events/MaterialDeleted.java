package org.example.inventory.infrastructure.messaging.events;

import java.time.Instant;
import java.util.UUID;

public record MaterialDeleted(UUID materialId, Instant occurredOn) {}
