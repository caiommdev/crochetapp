package org.example.budgeting.infrastructure.messaging.consumer;

import org.example.budgeting.infrastructure.cache.MaterialReadModelStore;
import org.example.budgeting.infrastructure.cache.MaterialType;
import org.example.budgeting.infrastructure.messaging.events.MaterialDefined;
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
public class MaterialDefinedListener {

    private static final Logger log = LoggerFactory.getLogger(MaterialDefinedListener.class);

    private final MaterialReadModelStore materialReadModelStore;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.material-defined", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.catalog}", type = "topic", durable = "true"),
            key = "material.defined"
    ))
    public void handle(MaterialDefined event, Message message) {
        CorrelationContext.withMessage(message, () -> {
            log.info("Evento material.defined recebido materialId={} name={}", event.materialId(), event.name());
            materialReadModelStore.putStatic(
                    event.materialId(), event.name(), MaterialType.valueOf(event.type()),
                    event.price(), event.color(), event.metersPerSkein());
            materialReadModelStore.putStock(event.materialId(), event.quantity(), event.meters());
            log.info("Read model de material atualizado materialId={}", event.materialId());
        });
    }
}
