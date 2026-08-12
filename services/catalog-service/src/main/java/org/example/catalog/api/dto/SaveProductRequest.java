package org.example.catalog.api.dto;

import org.example.catalog.domain.valueobjects.Image;

import java.util.UUID;

/**
 * Payload de criação/atualização de produto. O frontend envia o objeto {@code recipe} completo,
 * mas aqui só precisamos do id da receita (os demais campos são ignorados pelo Jackson).
 */
public record SaveProductRequest(
        String name,
        RecipeRef recipe,
        Image image
) {
    public record RecipeRef(UUID id) {}
}
