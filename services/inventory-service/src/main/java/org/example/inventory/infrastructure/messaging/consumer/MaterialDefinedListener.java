package org.example.inventory.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.inventory.api.dto.StockDto;
import org.example.inventory.application.StockService;
import org.example.inventory.infrastructure.messaging.events.MaterialDefined;
import org.example.inventory.infrastructure.observability.CorrelationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaterialDefinedListener {

    private static final Logger log = LoggerFactory.getLogger(MaterialDefinedListener.class);

    private final StockService stockService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "inventory.material-defined", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.catalog}", type = "topic", durable = "true"),
            key = "material.defined"
    ))
    public void handle(MaterialDefined event, Message message) {
        CorrelationContext.withMessage(message, () -> {
            log.info("Evento material.defined recebido materialId={} quantity={} meters={}",
                    event.materialId(), event.quantity(), event.meters());
            stockService.upsert(new StockDto(event.materialId(), event.quantity(), event.meters()));
        });
    }
}
