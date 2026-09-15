package org.example.budgeting.infrastructure.cache;

public record PointView(
        String name,
        Integer centimetersPerPoint,
        Integer quantity
) {}
