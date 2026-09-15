package org.example.inventory.infrastructure.messaging.rabbit;

import lombok.RequiredArgsConstructor;
import org.example.inventory.domain.shared.DomainEventPublisher;
import org.example.inventory.domain.shared.DomainEvents;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class RabbitDomainEventPublisher implements DomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitEventRouting routing;

    @Override
    public void publish(Collection<? extends DomainEvents> events) {
        events.forEach(event ->
                rabbitTemplate.convertAndSend(routing.exchange(), routing.routingKey(event.getClass()), event));
    }
}
