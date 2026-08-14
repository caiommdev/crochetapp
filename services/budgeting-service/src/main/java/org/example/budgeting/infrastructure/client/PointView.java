package org.example.budgeting.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PointView(
        String name,
        Integer centimetersPerPoint,
        Integer quantity
) {}
