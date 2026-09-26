package org.example.inventory.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.inventory.api.dto.ReservationRequest;
import org.example.inventory.api.dto.StockDto;
import org.example.inventory.domain.events.StockLevelChanged;
import org.example.inventory.domain.model.StockItem;
import org.example.inventory.domain.repository.StockItemRepository;
import org.example.inventory.domain.shared.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {

    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    private final StockItemRepository repository;
    private final DomainEventPublisher eventPublisher;

    public List<StockItem> findAll() {
        return repository.findAll();
    }

    public Optional<StockItem> findById(UUID materialId) {
        return repository.findById(materialId);
    }

    public List<StockItem> findByIds(List<UUID> materialIds) {
        return repository.findAllById(materialIds);
    }

    public StockItem upsert(StockDto dto) {
        log.info("Atualizando estoque materialId={} quantity={} meters={}", dto.materialId(), dto.quantity(), dto.meters());
        StockItem item = repository.findById(dto.materialId())
                .orElseGet(() -> StockItem.builder().materialId(dto.materialId()).build());
        item.setQuantity(dto.quantity());
        item.setMeters(dto.meters());
        StockItem saved = repository.save(item);
        eventPublisher.publish(List.of(new StockLevelChanged(saved.getMaterialId(), saved.getQuantity(), saved.getMeters())));
        log.info("Estoque persistido materialId={} quantity={} meters={}", saved.getMaterialId(), saved.getQuantity(), saved.getMeters());
        return saved;
    }

    public void deleteById(UUID materialId) {
        log.info("Removendo estoque materialId={}", materialId);
        repository.deleteById(materialId);
        log.info("Estoque removido materialId={}", materialId);
    }

    @Transactional
    public void reserve(ReservationRequest request) {
        log.info("Reservando materiais budgetId={} lineCount={}", request.budgetId(), request.lines().size());
        for (ReservationRequest.ReservationLine requestItem : request.lines()) {
            StockItem item = repository.findById(requestItem.materialId())
                    .orElseThrow(() -> {
                        log.warn("Falha na reserva: estoque não encontrado materialId={} budgetId={}", requestItem.materialId(), request.budgetId());
                        return new IllegalStateException(
                                "Estoque não encontrado para o material: " + requestItem.materialId());
                    });

            item.reserveItem(
                    requestItem.quantity(),
                    requestItem.meters()
            );

            repository.save(item);
            eventPublisher.publish(item.getEvents());
            log.info("Material reservado materialId={} budgetId={} quantity={} meters={}",
                    requestItem.materialId(), request.budgetId(), requestItem.quantity(), requestItem.meters());
        }
    }

    @Transactional
    public void release(ReservationRequest request) {
        log.info("Liberando materiais budgetId={} lineCount={}", request.budgetId(), request.lines().size());
        for (ReservationRequest.ReservationLine line : request.lines()) {
            Optional<StockItem> found = repository.findById(line.materialId());
            if (found.isEmpty()) {
                log.warn("Material não encontrado durante liberação materialId={} budgetId={}", line.materialId(), request.budgetId());
                continue;
            }
            StockItem item = found.get();

            if (line.quantity() != null && line.quantity() > 0) {
                int current = item.getQuantity() == null ? 0 : item.getQuantity();
                item.setQuantity(current + line.quantity());
            }
            if (line.meters() != null && line.meters() > 0) {
                int current = item.getMeters() == null ? 0 : item.getMeters();
                item.setMeters(current + line.meters());
            }
            StockItem saved = repository.save(item);
            eventPublisher.publish(List.of(new StockLevelChanged(saved.getMaterialId(), saved.getQuantity(), saved.getMeters())));
            log.info("Material liberado materialId={} budgetId={} quantity={} meters={}",
                    saved.getMaterialId(), request.budgetId(), line.quantity(), line.meters());
        }
    }
}
