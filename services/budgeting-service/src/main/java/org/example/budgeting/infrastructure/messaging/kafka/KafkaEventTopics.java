package org.example.budgeting.infrastructure.messaging.kafka;

import org.example.budgeting.domain.events.MaterialsReleaseRequested;
import org.example.budgeting.domain.events.MaterialsReservationRequested;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaEventTopics {

    private final Map<Class<?>, String> topicsByEventType;

    public KafkaEventTopics(
            @Value("${kafka.topic.reservation-requested}") String reservationRequested,
            @Value("${kafka.topic.reservation-released}") String reservationReleased
    ) {
        this.topicsByEventType = Map.<Class<?>, String>of(
                MaterialsReservationRequested.class, reservationRequested,
                MaterialsReleaseRequested.class, reservationReleased
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
