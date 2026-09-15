package org.example.budgeting.infrastructure.cache;

public record RequirementView(
        MaterialView material,
        Integer quantityNeeded
) {}
