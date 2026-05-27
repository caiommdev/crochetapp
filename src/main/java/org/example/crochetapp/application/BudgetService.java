package org.example.crochetapp.application;

import lombok.RequiredArgsConstructor;
import org.example.crochetapp.domain.model.Budget;
import org.example.crochetapp.infrastructure.repositories.BudgetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }

    public Optional<Budget> findById(UUID id) {
        return budgetRepository.findById(id);
    }

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Optional<Budget> update(UUID id, Budget updated) {
        return budgetRepository.findById(id).map(existing -> {
            existing.setProduct(updated.getProduct());
            existing.setMaterials(updated.getMaterials());
            existing.setStatus(updated.getStatus());
            return budgetRepository.save(existing);
        });
    }

    public void deleteById(UUID id) {
        budgetRepository.deleteById(id);
    }
}
