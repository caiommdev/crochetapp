package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.infrastructure.cache.MaterialReadModelStore;
import org.example.budgeting.infrastructure.messaging.events.MaterialDeleted;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaterialDeletedListener {

    private final MaterialReadModelStore materialReadModelStore;

    @KafkaListener(
            topics = "${kafka.topic.material-deleted}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "materialDeletedContainerFactory"
    )
    public void handle(MaterialDeleted event) {
        materialReadModelStore.remove(event.materialId());
    }
}
