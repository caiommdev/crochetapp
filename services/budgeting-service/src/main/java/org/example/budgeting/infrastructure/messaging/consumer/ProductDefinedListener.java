package org.example.budgeting.infrastructure.messaging.consumer;

import org.example.budgeting.infrastructure.cache.ProductReadModelStore;
import org.example.budgeting.infrastructure.messaging.events.ProductDefined;
import org.example.budgeting.infrastructure.observability.CorrelationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductDefinedListener {

    private static final Logger log = LoggerFactory.getLogger(ProductDefinedListener.class);

    private final ProductReadModelStore productReadModelStore;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.product-defined", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.catalog}", type = "topic", durable = "true"),
            key = "product.defined"
    ))
    public void handle(ProductDefined event, Message message) {
        CorrelationContext.withMessage(message, () -> {
            log.info("Evento product.defined recebido productId={} recipeId={}", event.productId(), event.recipeId());
            productReadModelStore.putDefined(event.productId(), event.name(), event.recipeId());
            log.info("Read model de produto atualizado productId={}", event.productId());
        });
    }
}
