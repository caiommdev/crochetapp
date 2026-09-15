package org.example.inventory.domain.shared;
import java.util.Collection;

public interface DomainEventPublisher {
    void publish(Collection<? extends DomainEvents> events);
}
