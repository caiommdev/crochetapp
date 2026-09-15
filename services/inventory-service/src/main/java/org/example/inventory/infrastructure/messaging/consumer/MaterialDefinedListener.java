package org.example.inventory.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.inventory.api.dto.StockDto;
import org.example.inventory.application.StockService;
import org.example.inventory.infrastructure.messaging.events.MaterialDefined;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaterialDefinedListener {

    private final StockService stockService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "inventory.material-defined", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.catalog}", type = "topic", durable = "true"),
            key = "material.defined"
    ))
    public void handle(MaterialDefined event) {
        stockService.upsert(new StockDto(event.materialId(), event.quantity(), event.meters()));
    }
}
