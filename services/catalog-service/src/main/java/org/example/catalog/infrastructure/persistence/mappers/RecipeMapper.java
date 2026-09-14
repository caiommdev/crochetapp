package org.example.catalog.infrastructure.persistence.mappers;

import lombok.RequiredArgsConstructor;
import org.example.catalog.domain.model.Recipe;
import org.example.catalog.domain.valueobjects.MaterialRequirement;
import org.example.catalog.domain.valueobjects.Point;
import org.example.catalog.infrastructure.persistence.entities.MaterialRequirementEmbeddable;
import org.example.catalog.infrastructure.persistence.entities.PointEmbeddable;
import org.example.catalog.infrastructure.persistence.entities.RecipeEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeMapper {

    private final ImageMapper imageMapper;

    public Recipe toDomain(RecipeEntity entity) {
        if (entity == null) return null;
        return Recipe.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .points(entity.getPoints().stream().map(this::toDomain).toList())
                .image(imageMapper.toDomain(entity.getImage()))
                .materialRequirements(entity.getMaterialRequirements().stream().map(this::toDomain).toList())
                .build();
    }

    public RecipeEntity toEntity(Recipe domain) {
        if (domain == null) return null;
        return RecipeEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .points(domain.getPoints().stream().map(this::toEmbeddable).toList())
                .image(imageMapper.toEmbeddable(domain.getImage()))
                .materialRequirements(domain.getMaterialRequirements().stream().map(this::toEmbeddable).toList())
                .build();
    }

    private Point toDomain(PointEmbeddable embeddable) {
        return new Point(embeddable.getName(), embeddable.getCentimetersPerPoint(), embeddable.getQuantity());
    }

    private PointEmbeddable toEmbeddable(Point domain) {
        return PointEmbeddable.builder()
                .name(domain.name())
                .centimetersPerPoint(domain.centimetersPerPoint())
                .quantity(domain.quantity())
                .build();
    }

    private MaterialRequirement toDomain(MaterialRequirementEmbeddable embeddable) {
        return new MaterialRequirement(embeddable.getMaterialId(), embeddable.getQuantityNeeded());
    }

    private MaterialRequirementEmbeddable toEmbeddable(MaterialRequirement domain) {
        return MaterialRequirementEmbeddable.builder()
                .materialId(domain.materialId())
                .quantityNeeded(domain.quantityNeeded())
                .build();
    }
}
