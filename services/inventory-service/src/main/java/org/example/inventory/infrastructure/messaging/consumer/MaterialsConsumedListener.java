package org.example.inventory.infrastructure.messaging.consumer;

import org.example.inventory.infrastructure.messaging.events.MaterialsConsumed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Fecha o ciclo de vida da reserva: o estoque já foi decrementado no momento em que a reserva
 * foi feita (StockItem.reserveItem), então aqui não há número a mudar — isso só registra, para
 * fins de auditoria/rastreio, que os materiais reservados para esse orçamento foram consumidos
 * de forma definitiva (em vez de liberados por um cancelamento).
 */
@Component
public class MaterialsConsumedListener {

    private static final Logger log = LoggerFactory.getLogger(MaterialsConsumedListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "inventory.materials-consumed", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.budgeting}", type = "topic", durable = "true"),
            key = "reservation.consumed"
    ))
    public void handle(MaterialsConsumed event) {
        log.info("Materiais do orçamento {} consumidos definitivamente: {}", event.budgetId(), event.lines());
    }
}
