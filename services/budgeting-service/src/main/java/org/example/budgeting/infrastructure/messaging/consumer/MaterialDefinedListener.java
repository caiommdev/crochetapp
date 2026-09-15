package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.infrastructure.cache.MaterialReadModelStore;
import org.example.budgeting.infrastructure.cache.MaterialType;
import org.example.budgeting.infrastructure.messaging.events.MaterialDefined;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaterialDefinedListener {

    private final MaterialReadModelStore materialReadModelStore;

    @KafkaListener(
            topics = "${kafka.topic.material-defined}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "materialDefinedContainerFactory"
    )
    public void handle(MaterialDefined event) {
        materialReadModelStore.putStatic(
                event.materialId(), event.name(), MaterialType.valueOf(event.type()),
                event.price(), event.color(), event.metersPerSkein());
        materialReadModelStore.putStock(event.materialId(), event.quantity(), event.meters());
    }
}
