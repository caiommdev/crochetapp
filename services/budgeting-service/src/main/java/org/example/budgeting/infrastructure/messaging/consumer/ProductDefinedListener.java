package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.infrastructure.cache.ProductReadModelStore;
import org.example.budgeting.infrastructure.messaging.events.ProductDefined;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductDefinedListener {

    private final ProductReadModelStore productReadModelStore;

    @KafkaListener(
            topics = "${kafka.topic.product-defined}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "productDefinedContainerFactory"
    )
    public void handle(ProductDefined event) {
        productReadModelStore.putDefined(event.productId(), event.name(), event.recipeId());
    }
}
