package org.example.catalog.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.example.catalog.api.dto.MaterialDto;
import org.example.catalog.api.dto.RecipeDto;
import org.example.catalog.api.dto.SaveRecipeRequest;
import org.example.catalog.domain.events.RecipeDefined;
import org.example.catalog.domain.events.RecipeDeleted;
import org.example.catalog.domain.model.Recipe;
import org.example.catalog.domain.repository.RecipeRepository;
import org.example.catalog.domain.shared.DomainEventPublisher;
import org.example.catalog.domain.valueobjects.MaterialRequirement;
import org.example.catalog.domain.valueobjects.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private static final Logger log = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository recipeRepository;
    private final MaterialService materialService;
    private final DomainEventPublisher eventPublisher;

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
        log.info("Criando receita name={} pointCount={} requirementCount={}",
            request.name(),
            request.points() == null ? 0 : request.points().size(),
            request.materialRequirements() == null ? 0 : request.materialRequirements().size());
        Recipe recipe = Recipe.builder()
                .name(request.name())
                .description(request.description())
                .points(buildPoints(request.points()))
                .materialRequirements(buildRequirements(request.materialRequirements()))
                .build();
        recipe = recipeRepository.save(recipe);
        publishDefined(recipe);
        log.info("Receita criada recipeId={} name={}", recipe.getId(), recipe.getName());
        return toDto(recipe);
    }

    @Transactional
    public Optional<RecipeDto> update(UUID id, SaveRecipeRequest request) {
        log.info("Atualizando receita recipeId={}", id);
        return recipeRepository.findById(id).map(existing -> {
            existing.setName(request.name());
            existing.setDescription(request.description());
            existing.getPoints().clear();
            existing.getPoints().addAll(buildPoints(request.points()));
            existing.getMaterialRequirements().clear();
            existing.getMaterialRequirements().addAll(buildRequirements(request.materialRequirements()));
            Recipe saved = recipeRepository.save(existing);
            publishDefined(saved);
            log.info("Receita atualizada recipeId={} name={}", saved.getId(), saved.getName());
            return toDto(saved);
        });
    }

    public void deleteById(UUID id) {
        log.info("Removendo receita recipeId={}", id);
        recipeRepository.deleteById(id);
        eventPublisher.publish(List.of(new RecipeDeleted(id)));
        log.info("Receita removida recipeId={}", id);
    }

    private void publishDefined(Recipe recipe) {
        List<RecipeDefined.PointItem> points = recipe.getPoints().stream()
                .map(p -> new RecipeDefined.PointItem(p.name(), p.centimetersPerPoint(), p.quantity()))
                .toList();
        List<RecipeDefined.RequirementItem> requirements = recipe.getMaterialRequirements().stream()
                .map(r -> new RecipeDefined.RequirementItem(r.materialId(), r.quantityNeeded()))
                .toList();
        eventPublisher.publish(List.of(new RecipeDefined(
                recipe.getId(), recipe.getName(), recipe.getDescription(), points, requirements)));
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
