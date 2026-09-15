package org.example.inventory.domain.shared;
import java.time.Instant;

public interface DomainEvents {
    Instant occurredOn();
}