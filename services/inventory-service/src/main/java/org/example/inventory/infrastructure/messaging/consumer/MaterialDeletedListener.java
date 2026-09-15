package org.example.inventory.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.inventory.application.StockService;
import org.example.inventory.infrastructure.messaging.events.MaterialDeleted;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaterialDeletedListener {

    private final StockService stockService;

    @KafkaListener(
            topics = "${kafka.topic.material-deleted}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "materialDeletedContainerFactory"
    )
    public void handle(MaterialDeleted event) {
        stockService.deleteById(event.materialId());
    }
}
