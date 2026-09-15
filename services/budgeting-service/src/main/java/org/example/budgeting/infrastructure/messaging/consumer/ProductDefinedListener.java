package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.infrastructure.cache.ProductReadModelStore;
import org.example.budgeting.infrastructure.messaging.events.ProductDefined;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductDefinedListener {

    private final ProductReadModelStore productReadModelStore;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.product-defined", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.catalog}", type = "topic", durable = "true"),
            key = "product.defined"
    ))
    public void handle(ProductDefined event) {
        productReadModelStore.putDefined(event.productId(), event.name(), event.recipeId());
    }
}
