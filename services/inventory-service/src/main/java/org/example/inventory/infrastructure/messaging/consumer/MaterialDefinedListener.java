package org.example.inventory.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.inventory.api.dto.StockDto;
import org.example.inventory.application.StockService;
import org.example.inventory.infrastructure.messaging.events.MaterialDefined;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaterialDefinedListener {

    private final StockService stockService;

    @KafkaListener(
            topics = "${kafka.topic.material-defined}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "materialDefinedContainerFactory"
    )
    public void handle(MaterialDefined event) {
        stockService.upsert(new StockDto(event.materialId(), event.quantity(), event.meters()));
    }
}
