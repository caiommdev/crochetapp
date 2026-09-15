package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.infrastructure.cache.MaterialReadModelStore;
import org.example.budgeting.infrastructure.messaging.events.StockLevelChanged;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockLevelChangedListener {

    private final MaterialReadModelStore materialReadModelStore;

    @KafkaListener(
            topics = "${kafka.topic.stock-level-changed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "stockLevelChangedContainerFactory"
    )
    public void handle(StockLevelChanged event) {
        materialReadModelStore.putStock(event.materialId(), event.quantity(), event.meters());
    }
}
