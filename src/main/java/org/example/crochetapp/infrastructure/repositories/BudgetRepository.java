package org.example.crochetapp.infrastructure.repositories;

import org.example.crochetapp.domain.model.Budget;
import org.example.crochetapp.domain.model.enums.BudgetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    List<Budget> findByStatus(BudgetStatus status);

    List<Budget> findByProductId(UUID productId);
}

