package org.example.inventory.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.inventory.api.dto.ReservationRequest;
import org.example.inventory.application.StockService;
import org.example.inventory.domain.events.ReservationProcessed;
import org.example.inventory.domain.shared.DomainEventPublisher;
import org.example.inventory.infrastructure.messaging.events.MaterialsReservationRequested;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaterialsReservationRequestedListener {

    private final StockService stockService;
    private final DomainEventPublisher eventPublisher;

    @KafkaListener(
            topics = "${kafka.topic.reservation-requested}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "materialsReservationRequestedContainerFactory"
    )
    public void handle(MaterialsReservationRequested event) {
        try {
            stockService.reserve(toReservationRequest(event));
            eventPublisher.publish(List.of(new ReservationProcessed(event.budgetId(), true, null)));
        } catch (IllegalStateException e) {
            eventPublisher.publish(List.of(new ReservationProcessed(event.budgetId(), false, e.getMessage())));
        }
    }

    private ReservationRequest toReservationRequest(MaterialsReservationRequested event) {
        List<ReservationRequest.ReservationLine> lines = event.lines().stream()
                .map(line -> new ReservationRequest.ReservationLine(line.materialId(), line.quantity(), line.meters()))
                .toList();
        return new ReservationRequest(event.budgetId(), lines);
    }
}
