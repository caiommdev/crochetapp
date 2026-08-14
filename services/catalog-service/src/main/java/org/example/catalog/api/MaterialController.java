package org.example.catalog.api;

import lombok.RequiredArgsConstructor;
import org.example.catalog.api.dto.MaterialDto;
import org.example.catalog.application.MaterialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    public ResponseEntity<List<MaterialDto>> findAll(
            @RequestParam(name = "ids", required = false) List<UUID> ids) {
        if (ids != null && !ids.isEmpty()) {
            return ResponseEntity.ok(materialService.findByIds(ids));
        }
        return ResponseEntity.ok(materialService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialDto> findById(@PathVariable UUID id) {
        return materialService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MaterialDto> create(@RequestBody MaterialDto material) {
        return ResponseEntity.ok(materialService.create(material));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialDto> update(@PathVariable UUID id, @RequestBody MaterialDto material) {
        return materialService.update(id, material)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        materialService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
