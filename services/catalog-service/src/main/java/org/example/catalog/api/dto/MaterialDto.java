package org.example.catalog.api.dto;

import org.example.catalog.domain.enums.MaterialType;
import org.example.catalog.domain.valueobjects.Image;

import java.math.BigDecimal;
import java.util.UUID;

public record MaterialDto(
        UUID id,
        String name,
        MaterialType type,
        BigDecimal price,
        Image image,
        String color,
        Integer quantity,
        Integer meters
) {}
