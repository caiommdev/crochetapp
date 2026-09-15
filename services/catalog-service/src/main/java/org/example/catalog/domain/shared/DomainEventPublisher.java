package org.example.catalog.domain.shared;

import java.util.Collection;

public interface DomainEventPublisher {
    void publish(Collection<? extends DomainEvents> events);
}
