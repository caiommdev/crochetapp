package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.infrastructure.cache.MaterialReadModelStore;
import org.example.budgeting.infrastructure.messaging.events.StockLevelChanged;
import org.example.budgeting.infrastructure.observability.CorrelationContext;
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
public class StockLevelChangedListener {

    private static final Logger log = LoggerFactory.getLogger(StockLevelChangedListener.class);

    private final MaterialReadModelStore materialReadModelStore;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.stock-level-changed", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.inventory}", type = "topic", durable = "true"),
            key = "stock.level.changed"
    ))
    public void handle(StockLevelChanged event, Message message) {
        CorrelationContext.withMessage(message, () -> {
            log.info("Evento stock.level.changed recebido materialId={} quantity={} meters={}",
                    event.materialId(), event.quantity(), event.meters());
            materialReadModelStore.putStock(event.materialId(), event.quantity(), event.meters());
            log.info("Estoque em cache atualizado materialId={}", event.materialId());
        });
    }
}
