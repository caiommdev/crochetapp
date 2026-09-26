package org.example.catalog.api;

import java.util.List;
import java.util.UUID;

import org.example.catalog.api.dto.RecipeDto;
import org.example.catalog.api.dto.SaveRecipeRequest;
import org.example.catalog.application.RecipeService;
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
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private static final Logger log = LoggerFactory.getLogger(RecipeController.class);

    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<List<RecipeDto>> findAll() {
        log.info("HTTP list recipes recebido");
        return ResponseEntity.ok(recipeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> findById(@PathVariable UUID id) {
        log.info("HTTP get recipe recebido recipeId={}", id);
        return recipeService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("HTTP get recipe retornando 404 recipeId={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<RecipeDto> create(@RequestBody SaveRecipeRequest request) {
        log.info("HTTP create recipe recebido name={}", request.name());
        return ResponseEntity.ok(recipeService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDto> update(@PathVariable UUID id, @RequestBody SaveRecipeRequest request) {
        log.info("HTTP update recipe recebido recipeId={} name={}", id, request.name());
        return recipeService.update(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("HTTP update recipe retornando 404 recipeId={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("HTTP delete recipe recebido recipeId={}", id);
        recipeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
