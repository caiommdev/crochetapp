package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.infrastructure.cache.ProductReadModelStore;
import org.example.budgeting.infrastructure.messaging.events.ProductDeleted;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductDeletedListener {

    private final ProductReadModelStore productReadModelStore;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.product-deleted", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.catalog}", type = "topic", durable = "true"),
            key = "product.deleted"
    ))
    public void handle(ProductDeleted event) {
        productReadModelStore.remove(event.productId());
    }
}
