package org.example.budgeting.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Visão de material vinda do Catalog (definição + estoque agregado).
 * Semântica de {@code meters}: YARN -> metros por novelo; METER_ACCESSORY -> metros em estoque.
 */
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
