package org.example.catalog.api.dto;

import org.example.catalog.domain.enums.MaterialType;
import org.example.catalog.domain.valueobjects.Image;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Representação de material exposta pela API. É a VISÃO AGREGADA: junta a definição
 * (dona do Catalog) com o estoque (dono do Inventory), preservando o contrato do frontend.
 *
 * Semântica do campo {@code meters} (herdada do modelo legado):
 *  - YARN            -> metros por novelo (definição)
 *  - METER_ACCESSORY -> metros disponíveis em estoque (Inventory)
 */
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
