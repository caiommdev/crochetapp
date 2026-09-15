package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.application.BudgetService;
import org.example.budgeting.infrastructure.messaging.events.ReservationProcessed;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationProcessedListener {

    private final BudgetService budgetService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.reservation-processed", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.inventory}", type = "topic", durable = "true"),
            key = "reservation.processed"
    ))
    public void handle(ReservationProcessed event) {
        budgetService.applyReservationResult(event.budgetId(), event.success(), event.reason());
    }
}
