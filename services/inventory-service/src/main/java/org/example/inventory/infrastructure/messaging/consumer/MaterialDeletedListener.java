package org.example.inventory.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.inventory.application.StockService;
import org.example.inventory.infrastructure.messaging.events.MaterialDeleted;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaterialDeletedListener {

    private final StockService stockService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "inventory.material-deleted", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.catalog}", type = "topic", durable = "true"),
            key = "material.deleted"
    ))
    public void handle(MaterialDeleted event) {
        stockService.deleteById(event.materialId());
    }
}
