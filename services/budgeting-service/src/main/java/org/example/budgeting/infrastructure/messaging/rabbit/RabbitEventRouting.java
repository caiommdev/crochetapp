package org.example.budgeting.infrastructure.messaging.rabbit;

import org.example.budgeting.domain.events.MaterialsConsumed;
import org.example.budgeting.domain.events.MaterialsReleaseRequested;
import org.example.budgeting.domain.events.MaterialsReservationRequested;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Exchange que o budgeting-service publica, e a routing key de cada tipo de evento. */
@Component
public class RabbitEventRouting {

    private final String exchange;
    private final Map<Class<?>, String> routingKeysByEventType;

    public RabbitEventRouting(@Value("${rabbitmq.exchange.budgeting}") String exchange) {
        this.exchange = exchange;
        this.routingKeysByEventType = Map.of(
                MaterialsReservationRequested.class, "reservation.requested",
                MaterialsReleaseRequested.class, "reservation.released",
                MaterialsConsumed.class, "reservation.consumed"
        );
    }

    public String exchange() {
        return exchange;
    }

    public String routingKey(Class<?> eventType) {
        String routingKey = routingKeysByEventType.get(eventType);
        if (routingKey == null) {
            throw new IllegalArgumentException("Nenhuma routing key configurada para o evento: " + eventType.getName());
        }
        return routingKey;
    }
}
