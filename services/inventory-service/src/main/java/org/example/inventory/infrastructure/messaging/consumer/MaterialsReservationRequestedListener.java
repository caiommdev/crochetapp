package org.example.inventory.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.inventory.api.dto.ReservationRequest;
import org.example.inventory.application.StockService;
import org.example.inventory.domain.events.ReservationProcessed;
import org.example.inventory.domain.shared.DomainEventPublisher;
import org.example.inventory.infrastructure.messaging.events.MaterialsReservationRequested;
import org.example.inventory.infrastructure.observability.CorrelationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaterialsReservationRequestedListener {

    private static final Logger log = LoggerFactory.getLogger(MaterialsReservationRequestedListener.class);

    private final StockService stockService;
    private final DomainEventPublisher eventPublisher;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "inventory.reservation-requested", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.budgeting}", type = "topic", durable = "true"),
            key = "reservation.requested"
    ))
    public void handle(MaterialsReservationRequested event, Message message) {
        CorrelationContext.withMessage(message, () -> {
            log.info("Evento reservation.requested recebido budgetId={} lineCount={}", event.budgetId(), event.lines().size());
            try {
                stockService.reserve(toReservationRequest(event));
                eventPublisher.publish(List.of(new ReservationProcessed(event.budgetId(), true, null)));
                log.info("Reserva de materiais concluída budgetId={}", event.budgetId());
            } catch (IllegalStateException e) {
                log.warn("Reserva de materiais falhou budgetId={} reason={}", event.budgetId(), e.getMessage());
                eventPublisher.publish(List.of(new ReservationProcessed(event.budgetId(), false, e.getMessage())));
            }
        });
    }

    private ReservationRequest toReservationRequest(MaterialsReservationRequested event) {
        List<ReservationRequest.ReservationLine> lines = event.lines().stream()
                .map(line -> new ReservationRequest.ReservationLine(line.materialId(), line.quantity(), line.meters()))
                .toList();
        return new ReservationRequest(event.budgetId(), lines);
    }
}
