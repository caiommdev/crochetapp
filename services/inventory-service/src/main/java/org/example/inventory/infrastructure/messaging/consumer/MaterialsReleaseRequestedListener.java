package org.example.inventory.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.inventory.api.dto.ReservationRequest;
import org.example.inventory.application.StockService;
import org.example.inventory.infrastructure.messaging.events.MaterialsReleaseRequested;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaterialsReleaseRequestedListener {

    private final StockService stockService;

    @KafkaListener(
            topics = "${kafka.topic.reservation-released}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "materialsReleaseRequestedContainerFactory"
    )
    public void handle(MaterialsReleaseRequested event) {
        List<ReservationRequest.ReservationLine> lines = event.lines().stream()
                .map(line -> new ReservationRequest.ReservationLine(line.materialId(), line.quantity(), line.meters()))
                .toList();
        stockService.release(new ReservationRequest(event.budgetId(), lines));
    }
}
