package org.example.budgeting.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeView(
        UUID id,
        String name,
        String description,
        List<PointView> points,
        Object image,
        List<RequirementView> materialRequirements
) {}
