package org.example.catalog.domain.shared;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {
    private List<DomainEvents> events;

    protected void addEvent(DomainEvents event) {
        if (events == null) {
            events = new ArrayList<>();
        }
        events.add(event);
    }

    public List<DomainEvents> getEvents() {
        return events;
    }

    protected void clearEvents() {
        if (events != null) {
            events.clear();
        }
    }
}
