package org.example.budgeting.application.dtos;

import org.example.budgeting.domain.enums.BudgetStatus;
import org.example.budgeting.infrastructure.client.MaterialView;
import org.example.budgeting.infrastructure.client.ProductView;

import java.util.List;
import java.util.UUID;

/**
 * Visão do orçamento devolvida ao frontend. O Budgeting guarda apenas ids; aqui os dados de
 * produto e materiais são enriquecidos (buscados no Catalog) para preservar o contrato da UI.
 */
public record BudgetDto(
        UUID id,
        ProductView product,
        List<MaterialView> materials,
        BudgetStatus status
) {}
