package org.example.inventory.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.inventory.api.dto.ReservationRequest;
import org.example.inventory.application.StockService;
import org.example.inventory.infrastructure.messaging.events.MaterialsReleaseRequested;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaterialsReleaseRequestedListener {

    private final StockService stockService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "inventory.reservation-released", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.budgeting}", type = "topic", durable = "true"),
            key = "reservation.released"
    ))
    public void handle(MaterialsReleaseRequested event) {
        List<ReservationRequest.ReservationLine> lines = event.lines().stream()
                .map(line -> new ReservationRequest.ReservationLine(line.materialId(), line.quantity(), line.meters()))
                .toList();
        stockService.release(new ReservationRequest(event.budgetId(), lines));
    }
}
