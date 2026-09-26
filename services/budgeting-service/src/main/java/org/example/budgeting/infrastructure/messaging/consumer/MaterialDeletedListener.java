package org.example.budgeting.infrastructure.messaging.consumer;

import org.example.budgeting.infrastructure.cache.MaterialReadModelStore;
import org.example.budgeting.infrastructure.messaging.events.MaterialDeleted;
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
public class MaterialDeletedListener {

    private static final Logger log = LoggerFactory.getLogger(MaterialDeletedListener.class);

    private final MaterialReadModelStore materialReadModelStore;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.material-deleted", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.catalog}", type = "topic", durable = "true"),
            key = "material.deleted"
    ))
    public void handle(MaterialDeleted event, Message message) {
        CorrelationContext.withMessage(message, () -> {
            log.info("Evento material.deleted recebido materialId={}", event.materialId());
            materialReadModelStore.remove(event.materialId());
            log.info("Read model de material removido materialId={}", event.materialId());
        });
    }
}
