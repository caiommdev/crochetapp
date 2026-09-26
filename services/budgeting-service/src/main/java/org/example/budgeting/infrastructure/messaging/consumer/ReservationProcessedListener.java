package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.application.BudgetService;
import org.example.budgeting.infrastructure.messaging.events.ReservationProcessed;
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
public class ReservationProcessedListener {

    private static final Logger log = LoggerFactory.getLogger(ReservationProcessedListener.class);

    private final BudgetService budgetService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.reservation-processed", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.inventory}", type = "topic", durable = "true"),
            key = "reservation.processed"
    ))
    public void handle(ReservationProcessed event, Message message) {
        CorrelationContext.withMessage(message, () -> {
            log.info("Evento reservation.processed recebido budgetId={} success={} reason={}",
                    event.budgetId(), event.success(), event.reason());
            budgetService.applyReservationResult(event.budgetId(), event.success(), event.reason());
        });
    }
}
