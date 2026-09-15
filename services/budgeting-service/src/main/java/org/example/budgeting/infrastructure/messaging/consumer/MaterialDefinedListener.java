package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.infrastructure.cache.MaterialReadModelStore;
import org.example.budgeting.infrastructure.cache.MaterialType;
import org.example.budgeting.infrastructure.messaging.events.MaterialDefined;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaterialDefinedListener {

    private final MaterialReadModelStore materialReadModelStore;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.material-defined", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.catalog}", type = "topic", durable = "true"),
            key = "material.defined"
    ))
    public void handle(MaterialDefined event) {
        materialReadModelStore.putStatic(
                event.materialId(), event.name(), MaterialType.valueOf(event.type()),
                event.price(), event.color(), event.metersPerSkein());
        materialReadModelStore.putStock(event.materialId(), event.quantity(), event.meters());
    }
}
