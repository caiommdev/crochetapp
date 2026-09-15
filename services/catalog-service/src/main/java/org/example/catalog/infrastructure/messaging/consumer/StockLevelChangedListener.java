package org.example.catalog.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.catalog.infrastructure.cache.MaterialStockCache;
import org.example.catalog.infrastructure.messaging.events.StockLevelChanged;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockLevelChangedListener {

    private final MaterialStockCache stockCache;

    @KafkaListener(
            topics = "${kafka.topic.stock-level-changed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "stockLevelChangedContainerFactory"
    )
    public void handle(StockLevelChanged event) {
        stockCache.put(event.materialId(), event.quantity(), event.meters());
    }
}
