package org.example.budgeting.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MaterialView(
        UUID id,
        String name,
        MaterialType type,
        BigDecimal price,
        Object image,
        String color,
        Integer quantity,
        Integer meters
) {}
