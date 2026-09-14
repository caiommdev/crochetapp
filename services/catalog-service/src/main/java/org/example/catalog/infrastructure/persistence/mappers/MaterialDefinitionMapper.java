package org.example.catalog.infrastructure.persistence.mappers;

import lombok.RequiredArgsConstructor;
import org.example.catalog.domain.model.MaterialDefinition;
import org.example.catalog.infrastructure.persistence.entities.MaterialDefinitionEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaterialDefinitionMapper {

    private final ImageMapper imageMapper;

    public MaterialDefinition toDomain(MaterialDefinitionEntity entity) {
        if (entity == null) return null;
        return MaterialDefinition.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .type(entity.getType())
                .image(imageMapper.toDomain(entity.getImage()))
                .color(entity.getColor())
                .metersPerSkein(entity.getMetersPerSkein())
                .build();
    }

    public MaterialDefinitionEntity toEntity(MaterialDefinition domain) {
        if (domain == null) return null;
        return MaterialDefinitionEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .price(domain.getPrice())
                .type(domain.getType())
                .image(imageMapper.toEmbeddable(domain.getImage()))
                .color(domain.getColor())
                .metersPerSkein(domain.getMetersPerSkein())
                .build();
    }
}
