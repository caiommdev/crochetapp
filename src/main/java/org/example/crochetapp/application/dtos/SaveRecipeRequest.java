package org.example.crochetapp.application.dtos;

import java.util.List;
import java.util.UUID;

public record SaveRecipeRequest(
        String name,
        String description,
        List<PointDto> points,
        List<MaterialRequirementDto> materialRequirements
) {
    public record PointDto(String name, Integer centimetersPerPoint) {}
    public record MaterialRequirementDto(UUID materialId, Integer quantityNeeded) {}
}
