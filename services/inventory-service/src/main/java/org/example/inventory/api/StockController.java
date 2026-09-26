package org.example.inventory.api;

import java.util.List;
import java.util.UUID;

import org.example.inventory.api.dto.StockDto;
import org.example.inventory.domain.model.StockItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private static final Logger log = LoggerFactory.getLogger(StockController.class);

    private final org.example.inventory.application.StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockItem>> findAll(@RequestParam(name = "ids", required = false) List<UUID> ids) {
        log.info("HTTP list stock recebido idsFilterCount={}", ids == null ? 0 : ids.size());
        if (ids != null && !ids.isEmpty()) {
            return ResponseEntity.ok(stockService.findByIds(ids));
        }
        return ResponseEntity.ok(stockService.findAll());
    }

    @GetMapping("/{materialId}")
    public ResponseEntity<StockItem> findById(@PathVariable UUID materialId) {
        log.info("HTTP get stock recebido materialId={}", materialId);
        return stockService.findById(materialId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("HTTP get stock retornando 404 materialId={}", materialId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<StockItem> upsert(@RequestBody StockDto dto) {
        log.info("HTTP upsert stock recebido materialId={} quantity={} meters={}", dto.materialId(), dto.quantity(), dto.meters());
        return ResponseEntity.ok(stockService.upsert(dto));
    }

    @PutMapping("/{materialId}")
    public ResponseEntity<StockItem> update(@PathVariable UUID materialId, @RequestBody StockDto dto) {
        log.info("HTTP update stock recebido materialId={} quantity={} meters={}", materialId, dto.quantity(), dto.meters());
        StockDto normalized = new StockDto(materialId, dto.quantity(), dto.meters());
        return ResponseEntity.ok(stockService.upsert(normalized));
    }

    @DeleteMapping("/{materialId}")
    public ResponseEntity<Void> delete(@PathVariable UUID materialId) {
        log.info("HTTP delete stock recebido materialId={}", materialId);
        stockService.deleteById(materialId);
        return ResponseEntity.noContent().build();
    }
}
