package org.example.budgeting.infrastructure.messaging.events;

import java.time.Instant;
import java.util.UUID;

public record ReservationProcessed(UUID budgetId, boolean success, String reason, Instant occurredOn) {}
