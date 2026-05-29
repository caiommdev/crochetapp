package org.example.crochetapp.application;

import lombok.RequiredArgsConstructor;
import org.example.crochetapp.application.dtos.SaveRecipeRequest;
import org.example.crochetapp.domain.model.Recipe;
import org.example.crochetapp.domain.model.material.Material;
import org.example.crochetapp.domain.model.valueobjects.Point;
import org.example.crochetapp.domain.model.valueobjects.RecipeMaterialRequirement;
import org.example.crochetapp.infrastructure.repositories.MaterialRepository;
import org.example.crochetapp.infrastructure.repositories.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final MaterialRepository materialRepository;

    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> findById(UUID id) {
        return recipeRepository.findById(id);
    }

    @Transactional
    public Recipe save(SaveRecipeRequest request) {
        Recipe recipe = Recipe.builder()
                .name(request.name())
                .description(request.description())
                .points(buildPoints(request.points()))
                .materialRequirements(buildRequirements(request.materialRequirements()))
                .build();
        return recipeRepository.save(recipe);
    }

    @Transactional
    public Optional<Recipe> update(UUID id, SaveRecipeRequest request) {
        return recipeRepository.findById(id).map(existing -> {
            existing.setName(request.name());
            existing.setDescription(request.description());
            // clear+addAll to let Hibernate track changes on @ElementCollection
            existing.getPoints().clear();
            existing.getPoints().addAll(buildPoints(request.points()));
            existing.getMaterialRequirements().clear();
            existing.getMaterialRequirements().addAll(buildRequirements(request.materialRequirements()));
            return recipeRepository.save(existing);
        });
    }

    public void deleteById(UUID id) {
        recipeRepository.deleteById(id);
    }

    private List<Point> buildPoints(List<SaveRecipeRequest.PointDto> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream()
                .map(d -> Point.builder()
                        .name(d.name())
                        .centimetersPerPoint(d.centimetersPerPoint())
                        .build())
                .toList();
    }

    private List<RecipeMaterialRequirement> buildRequirements(
            List<SaveRecipeRequest.MaterialRequirementDto> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream()
                .map(d -> {
                    Material material = materialRepository.findById(d.materialId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Material não encontrado: " + d.materialId()));
                    return RecipeMaterialRequirement.builder()
                            .material(material)
                            .quantityNeeded(d.quantityNeeded())
                            .build();
                })
                .toList();
    }
}
