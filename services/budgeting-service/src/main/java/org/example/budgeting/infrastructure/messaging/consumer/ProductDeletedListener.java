package org.example.budgeting.infrastructure.messaging.consumer;

import org.example.budgeting.infrastructure.cache.ProductReadModelStore;
import org.example.budgeting.infrastructure.messaging.events.ProductDeleted;
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
public class ProductDeletedListener {

    private static final Logger log = LoggerFactory.getLogger(ProductDeletedListener.class);

    private final ProductReadModelStore productReadModelStore;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.product-deleted", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.catalog}", type = "topic", durable = "true"),
            key = "product.deleted"
    ))
    public void handle(ProductDeleted event, Message message) {
        CorrelationContext.withMessage(message, () -> {
            log.info("Evento product.deleted recebido productId={}", event.productId());
            productReadModelStore.remove(event.productId());
            log.info("Read model de produto removido productId={}", event.productId());
        });
    }
}
