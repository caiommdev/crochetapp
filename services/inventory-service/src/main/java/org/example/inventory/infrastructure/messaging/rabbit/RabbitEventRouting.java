package org.example.inventory.infrastructure.messaging.rabbit;

import org.example.inventory.domain.events.ReservationProcessed;
import org.example.inventory.domain.events.StockLevelChanged;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Exchange que o inventory-service publica, e a routing key de cada tipo de evento. */
@Component
public class RabbitEventRouting {

    private final String exchange;
    private final Map<Class<?>, String> routingKeysByEventType;

    public RabbitEventRouting(@Value("${rabbitmq.exchange.inventory}") String exchange) {
        this.exchange = exchange;
        this.routingKeysByEventType = Map.of(
                StockLevelChanged.class, "stock.level.changed",
                ReservationProcessed.class, "reservation.processed"
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
