package org.example.catalog.api;

import java.util.List;
import java.util.UUID;

import org.example.catalog.api.dto.MaterialDto;
import org.example.catalog.application.MaterialService;
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
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private static final Logger log = LoggerFactory.getLogger(MaterialController.class);

    private final MaterialService materialService;

    @GetMapping
    public ResponseEntity<List<MaterialDto>> findAll(
            @RequestParam(name = "ids", required = false) List<UUID> ids) {
        log.info("HTTP list materials recebido idsFilterCount={}", ids == null ? 0 : ids.size());
        if (ids != null && !ids.isEmpty()) {
            return ResponseEntity.ok(materialService.findByIds(ids));
        }
        return ResponseEntity.ok(materialService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialDto> findById(@PathVariable UUID id) {
        log.info("HTTP get material recebido materialId={}", id);
        return materialService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("HTTP get material retornando 404 materialId={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<MaterialDto> create(@RequestBody MaterialDto material) {
        log.info("HTTP create material recebido name={} type={}", material.name(), material.type());
        return ResponseEntity.ok(materialService.create(material));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialDto> update(@PathVariable UUID id, @RequestBody MaterialDto material) {
        log.info("HTTP update material recebido materialId={} name={} type={}", id, material.name(), material.type());
        return materialService.update(id, material)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("HTTP update material retornando 404 materialId={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("HTTP delete material recebido materialId={}", id);
        materialService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
