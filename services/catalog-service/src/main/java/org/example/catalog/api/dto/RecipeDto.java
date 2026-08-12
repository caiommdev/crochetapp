package org.example.catalog.api.dto;

import org.example.catalog.domain.valueobjects.Image;
import org.example.catalog.domain.valueobjects.Point;

import java.util.List;
import java.util.UUID;

public record RecipeDto(
        UUID id,
        String name,
        String description,
        List<Point> points,
        Image image,
        List<RequirementDto> materialRequirements
) {
    public record RequirementDto(
            MaterialDto material,
            Integer quantityNeeded
    ) {}
}
