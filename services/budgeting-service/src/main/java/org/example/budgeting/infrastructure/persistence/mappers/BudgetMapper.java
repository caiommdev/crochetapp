package org.example.budgeting.infrastructure.persistence.mappers;

import org.example.budgeting.domain.model.Budget;
import org.example.budgeting.infrastructure.persistence.entities.BudgetEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class BudgetMapper {

    public Budget toDomain(BudgetEntity entity) {
        if (entity == null) return null;
        return Budget.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .materialIds(new HashSet<>(entity.getMaterialIds()))
                .status(entity.getStatus())
                .build();
    }

    public BudgetEntity toEntity(Budget domain) {
        if (domain == null) return null;
        return BudgetEntity.builder()
                .id(domain.getId())
                .productId(domain.getProductId())
                .materialIds(new HashSet<>(domain.getMaterialIds()))
                .status(domain.getStatus())
                .build();
    }
}
