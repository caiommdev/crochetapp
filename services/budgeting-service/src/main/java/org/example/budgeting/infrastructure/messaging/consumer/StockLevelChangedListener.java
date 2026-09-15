package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.infrastructure.cache.MaterialReadModelStore;
import org.example.budgeting.infrastructure.messaging.events.StockLevelChanged;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockLevelChangedListener {

    private final MaterialReadModelStore materialReadModelStore;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.stock-level-changed", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.inventory}", type = "topic", durable = "true"),
            key = "stock.level.changed"
    ))
    public void handle(StockLevelChanged event) {
        materialReadModelStore.putStock(event.materialId(), event.quantity(), event.meters());
    }
}
