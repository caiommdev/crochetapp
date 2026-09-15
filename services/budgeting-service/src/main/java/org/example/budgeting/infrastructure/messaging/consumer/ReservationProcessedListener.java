package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.application.BudgetService;
import org.example.budgeting.infrastructure.messaging.events.ReservationProcessed;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationProcessedListener {

    private final BudgetService budgetService;

    @KafkaListener(
            topics = "${kafka.topic.reservation-processed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "reservationProcessedContainerFactory"
    )
    public void handle(ReservationProcessed event) {
        budgetService.applyReservationResult(event.budgetId(), event.success(), event.reason());
    }
}
