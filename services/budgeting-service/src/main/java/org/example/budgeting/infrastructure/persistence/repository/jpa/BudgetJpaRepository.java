package org.example.budgeting.infrastructure.persistence.repository.jpa;

import org.example.budgeting.infrastructure.persistence.entities.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BudgetJpaRepository extends JpaRepository<BudgetEntity, UUID> {
}
