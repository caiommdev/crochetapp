package org.example.inventory.api;

import lombok.RequiredArgsConstructor;
import org.example.inventory.api.dto.ReservationRequest;
import org.example.inventory.api.dto.StockDto;
import org.example.inventory.domain.StockItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final org.example.inventory.application.StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockItem>> findAll(@RequestParam(name = "ids", required = false) List<UUID> ids) {
        if (ids != null && !ids.isEmpty()) {
            return ResponseEntity.ok(stockService.findByIds(ids));
        }
        return ResponseEntity.ok(stockService.findAll());
    }

    @GetMapping("/{materialId}")
    public ResponseEntity<StockItem> findById(@PathVariable UUID materialId) {
        return stockService.findById(materialId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StockItem> upsert(@RequestBody StockDto dto) {
        return ResponseEntity.ok(stockService.upsert(dto));
    }

    @PutMapping("/{materialId}")
    public ResponseEntity<StockItem> update(@PathVariable UUID materialId, @RequestBody StockDto dto) {
        StockDto normalized = new StockDto(materialId, dto.quantity(), dto.meters());
        return ResponseEntity.ok(stockService.upsert(normalized));
    }

    @DeleteMapping("/{materialId}")
    public ResponseEntity<Void> delete(@PathVariable UUID materialId) {
        stockService.deleteById(materialId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserve(@RequestBody ReservationRequest request) {
        stockService.reserve(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/release")
    public ResponseEntity<Void> release(@RequestBody ReservationRequest request) {
        stockService.release(request);
        return ResponseEntity.noContent().build();
    }
}
