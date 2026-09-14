package org.example.catalog.infrastructure.persistence.mappers;

import lombok.RequiredArgsConstructor;
import org.example.catalog.domain.model.Product;
import org.example.catalog.infrastructure.persistence.entities.ProductEntity;
import org.example.catalog.infrastructure.persistence.entities.RecipeEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ImageMapper imageMapper;
    private final RecipeMapper recipeMapper;

    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .recipe(recipeMapper.toDomain(entity.getRecipe()))
                .image(imageMapper.toDomain(entity.getImage()))
                .build();
    }

    public ProductEntity toEntity(Product domain) {
        if (domain == null) return null;
        return ProductEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .recipe(RecipeEntity.builder().id(domain.getRecipe().getId()).build())
                .image(imageMapper.toEmbeddable(domain.getImage()))
                .build();
    }
}
