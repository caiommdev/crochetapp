package org.example.inventory.infrastructure.messaging.rabbit;

import java.util.Collection;

import org.example.inventory.domain.shared.DomainEventPublisher;
import org.example.inventory.domain.shared.DomainEvents;
import org.example.inventory.infrastructure.observability.CorrelationContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RabbitDomainEventPublisher implements DomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitEventRouting routing;

    @Override
    public void publish(Collection<? extends DomainEvents> events) {
        events.forEach(event ->
                rabbitTemplate.convertAndSend(
                        routing.exchange(),
                        routing.routingKey(event.getClass()),
                        event,
                        CorrelationContext.outboundHeaders()));
    }
}
