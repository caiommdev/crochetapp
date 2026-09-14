package org.example.budgeting.domain.repository;

import org.example.budgeting.domain.model.Budget;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository {

    List<Budget> findAll();

    Optional<Budget> findById(UUID id);

    Budget save(Budget budget);

    void deleteById(UUID id);
}
