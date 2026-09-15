package org.example.budgeting.infrastructure.messaging.kafka;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.domain.shared.DomainEventPublisher;
import org.example.budgeting.domain.shared.DomainEvents;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaEventTopics topics;

    @Override
    public void publish(Collection<? extends DomainEvents> events) {
        events.forEach(event -> kafkaTemplate.send(topics.resolve(event.getClass()), event));
    }
}
