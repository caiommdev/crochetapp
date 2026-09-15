package org.example.catalog.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.catalog.infrastructure.cache.MaterialStockCache;
import org.example.catalog.infrastructure.messaging.events.StockLevelChanged;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockLevelChangedListener {

    private final MaterialStockCache stockCache;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "catalog.stock-level-changed", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.inventory}", type = "topic", durable = "true"),
            key = "stock.level.changed"
    ))
    public void handle(StockLevelChanged event) {
        stockCache.put(event.materialId(), event.quantity(), event.meters());
    }
}
