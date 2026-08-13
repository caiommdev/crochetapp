package org.example.inventory.application;

import lombok.RequiredArgsConstructor;
import org.example.inventory.api.dto.ReservationRequest;
import org.example.inventory.api.dto.StockDto;
import org.example.inventory.domain.StockItem;
import org.example.inventory.infrastructure.StockItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockItemRepository repository;

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
        StockItem item = repository.findById(dto.materialId())
                .orElseGet(() -> StockItem.builder().materialId(dto.materialId()).build());
        item.setQuantity(dto.quantity());
        item.setMeters(dto.meters());
        return repository.save(item);
    }

    public void deleteById(UUID materialId) {
        repository.deleteById(materialId);
    }

    @Transactional
    public void reserve(ReservationRequest request) {
        for (ReservationRequest.ReservationLine line : request.lines()) {
            StockItem item = repository.findById(line.materialId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Estoque não encontrado para o material: " + line.materialId()));

            if (line.quantity() != null && line.quantity() > 0) {
                int current = item.getQuantity() == null ? 0 : item.getQuantity();
                if (current < line.quantity()) {
                    throw new IllegalStateException("Estoque insuficiente (unidades) para o material "
                            + line.materialId() + ". Necessário: " + line.quantity() + ", disponível: " + current);
                }
                item.setQuantity(current - line.quantity());
            }

            if (line.meters() != null && line.meters() > 0) {
                int current = item.getMeters() == null ? 0 : item.getMeters();
                if (current < line.meters()) {
                    throw new IllegalStateException("Estoque insuficiente (metros) para o material "
                            + line.materialId() + ". Necessário: " + line.meters() + "m, disponível: " + current + "m");
                }
                item.setMeters(current - line.meters());
            }

            repository.save(item);
        }
    }

    @Transactional
    public void release(ReservationRequest request) {
        for (ReservationRequest.ReservationLine line : request.lines()) {
            Optional<StockItem> found = repository.findById(line.materialId());
            if (found.isEmpty()) continue;
            StockItem item = found.get();

            if (line.quantity() != null && line.quantity() > 0) {
                int current = item.getQuantity() == null ? 0 : item.getQuantity();
                item.setQuantity(current + line.quantity());
            }
            if (line.meters() != null && line.meters() > 0) {
                int current = item.getMeters() == null ? 0 : item.getMeters();
                item.setMeters(current + line.meters());
            }
            repository.save(item);
        }
    }
}
