package org.example.budgeting.infrastructure.messaging.consumer;

import org.example.budgeting.infrastructure.cache.ProductReadModelStore;
import org.example.budgeting.infrastructure.messaging.events.ProductDeleted;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductDeletedListener {

    private final ProductReadModelStore productReadModelStore;

    @KafkaListener(
            topics = "${kafka.topic.product-deleted}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "productDeletedContainerFactory"
    )
    public void handle(ProductDeleted event) {
        productReadModelStore.remove(event.productId());
    }
}
