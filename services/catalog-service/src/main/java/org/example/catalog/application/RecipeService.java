package org.example.catalog.application;

import lombok.RequiredArgsConstructor;
import org.example.catalog.api.dto.MaterialDto;
import org.example.catalog.api.dto.RecipeDto;
import org.example.catalog.api.dto.SaveRecipeRequest;
import org.example.catalog.domain.model.Recipe;
import org.example.catalog.domain.valueobjects.MaterialRequirement;
import org.example.catalog.domain.valueobjects.Point;
import org.example.catalog.domain.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final MaterialService materialService;

    @Transactional(readOnly = true)
    public List<RecipeDto> findAll() {
        return recipeRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<RecipeDto> findById(UUID id) {
        return recipeRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public RecipeDto save(SaveRecipeRequest request) {
        Recipe recipe = Recipe.builder()
                .name(request.name())
                .description(request.description())
                .points(buildPoints(request.points()))
                .materialRequirements(buildRequirements(request.materialRequirements()))
                .build();
        return toDto(recipeRepository.save(recipe));
    }

    @Transactional
    public Optional<RecipeDto> update(UUID id, SaveRecipeRequest request) {
        return recipeRepository.findById(id).map(existing -> {
            existing.setName(request.name());
            existing.setDescription(request.description());
            existing.getPoints().clear();
            existing.getPoints().addAll(buildPoints(request.points()));
            existing.getMaterialRequirements().clear();
            existing.getMaterialRequirements().addAll(buildRequirements(request.materialRequirements()));
            return toDto(recipeRepository.save(existing));
        });
    }

    public void deleteById(UUID id) {
        recipeRepository.deleteById(id);
    }

    public RecipeDto toDto(Recipe recipe) {
        List<UUID> materialIds = recipe.getMaterialRequirements().stream()
                .map(MaterialRequirement::materialId)
                .toList();
        Map<UUID, MaterialDto> materials = materialService.findAsDtoMap(materialIds);

        List<RecipeDto.RequirementDto> requirements = recipe.getMaterialRequirements().stream()
                .map(req -> new RecipeDto.RequirementDto(
                        materials.get(req.materialId()),
                        req.quantityNeeded()))
                .toList();

        return new RecipeDto(
                recipe.getId(), recipe.getName(), recipe.getDescription(),
                recipe.getPoints(), recipe.getImage(), requirements);
    }

    private List<Point> buildPoints(List<SaveRecipeRequest.PointDto> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream()
                .map(d -> new Point(d.name(), d.centimetersPerPoint(), d.quantity()))
                .toList();
    }

    private List<MaterialRequirement> buildRequirements(List<SaveRecipeRequest.MaterialRequirementDto> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream()
                .map(d -> new MaterialRequirement(d.materialId(), d.quantityNeeded()))
                .toList();
    }
}
