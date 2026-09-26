package org.example.inventory.infrastructure.messaging.consumer;

import org.example.inventory.infrastructure.messaging.events.MaterialsConsumed;
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
public class MaterialsConsumedListener {

    private static final Logger log = LoggerFactory.getLogger(MaterialsConsumedListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "inventory.materials-consumed", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.budgeting}", type = "topic", durable = "true"),
            key = "reservation.consumed"
    ))
    public void handle(MaterialsConsumed event, Message message) {
        CorrelationContext.withMessage(message, () ->
                log.info("Evento reservation.consumed recebido budgetId={} lineCount={}",
                        event.budgetId(), event.lines().size()));
    }
}
