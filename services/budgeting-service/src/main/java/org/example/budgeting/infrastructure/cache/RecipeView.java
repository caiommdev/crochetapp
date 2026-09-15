package org.example.budgeting.infrastructure.cache;

import java.util.List;
import java.util.UUID;

public record RecipeView(
        UUID id,
        String name,
        String description,
        List<PointView> points,
        Object image,
        List<RequirementView> materialRequirements
) {}
