package org.example.inventory.infrastructure.messaging.kafka;

import org.example.inventory.domain.events.ReservationProcessed;
import org.example.inventory.domain.events.StockLevelChanged;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaEventTopics {

    private final Map<Class<?>, String> topicsByEventType;

    public KafkaEventTopics(
            @Value("${kafka.topic.stock-level-changed}") String stockLevelChanged,
            @Value("${kafka.topic.reservation-processed}") String reservationProcessed
    ) {
        this.topicsByEventType = Map.of(
                StockLevelChanged.class, stockLevelChanged,
                ReservationProcessed.class, reservationProcessed
        );
    }

    public String resolve(Class<?> eventType) {
        String topic = topicsByEventType.get(eventType);
        if (topic == null) {
            throw new IllegalArgumentException("Nenhum tópico Kafka configurado para o evento: " + eventType.getName());
        }
        return topic;
    }
}
