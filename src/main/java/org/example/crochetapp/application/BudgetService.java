package org.example.crochetapp.application;

import lombok.RequiredArgsConstructor;

import org.example.crochetapp.application.dtos.BudgetQuote;
import org.example.crochetapp.domain.service.pricing.ProfitRange;
import org.example.crochetapp.domain.model.Budget;
import org.example.crochetapp.domain.model.Product;
import org.example.crochetapp.domain.model.enums.BudgetStatus;
import org.example.crochetapp.domain.model.material.Material;
import org.example.crochetapp.domain.service.MaterialReservationService;
import org.example.crochetapp.domain.service.feasibility.BudgetFeasibilityService;
import org.example.crochetapp.domain.service.feasibility.FeasibilityResult;
import org.example.crochetapp.domain.service.pricing.BudgetPricingService;
import org.example.crochetapp.infrastructure.repositories.BudgetRepository;
import org.example.crochetapp.infrastructure.repositories.MaterialRepository;
import org.example.crochetapp.infrastructure.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ProductRepository productRepository;
    private final MaterialRepository materialRepository;
    private final BudgetFeasibilityService feasibilityService;
    private final BudgetPricingService pricingService;
    private final MaterialReservationService materialReservationService;

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

    @Transactional
    public BudgetQuote createQuote(UUID productId, List<UUID> materialIds) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        List<Material> materials = materialRepository.findAllById(materialIds);

        FeasibilityResult feasibility = feasibilityService.checkFeasibility(product, materials);
        if (!feasibility.feasible()) throw new RuntimeException("Orçamento inviável: " + feasibility.reason());

        List<ProfitRange> ranges = pricingService.calculateProfitRanges(product, materials);

        Budget budget = Budget.builder()
            .product(product)
            .materials(materials)
            .status(BudgetStatus.IN_VALIDATION)
            .build();

        budgetRepository.save(budget);
        return new BudgetQuote(budget, ranges);
    }
    
    public void acceptBudget(UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
        budget.confirm();
        materialReservationService.reserve(budget);
        budgetRepository.save(budget);
    }

    public void cancelBudget(UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
        budget.cancel();
        materialReservationService.release(budget); // devolve materiais
        budgetRepository.save(budget);
    }
}


