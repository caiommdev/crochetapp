package org.example.catalog.api;

import lombok.RequiredArgsConstructor;
import org.example.catalog.api.dto.RecipeDto;
import org.example.catalog.api.dto.SaveRecipeRequest;
import org.example.catalog.application.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<List<RecipeDto>> findAll() {
        return ResponseEntity.ok(recipeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> findById(@PathVariable UUID id) {
        return recipeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RecipeDto> create(@RequestBody SaveRecipeRequest request) {
        return ResponseEntity.ok(recipeService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDto> update(@PathVariable UUID id, @RequestBody SaveRecipeRequest request) {
        return recipeService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        recipeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
