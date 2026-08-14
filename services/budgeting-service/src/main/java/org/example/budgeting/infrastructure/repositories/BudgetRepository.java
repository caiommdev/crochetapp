package org.example.budgeting.infrastructure.repositories;

import org.example.budgeting.domain.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {
}
