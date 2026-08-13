package org.example.budgeting.application.dtos;

import org.example.budgeting.domain.enums.BudgetStatus;
import org.example.budgeting.infrastructure.client.MaterialView;
import org.example.budgeting.infrastructure.client.ProductView;

import java.util.List;
import java.util.UUID;

public record BudgetDto(
        UUID id,
        ProductView product,
        List<MaterialView> materials,
        BudgetStatus status
) {}
