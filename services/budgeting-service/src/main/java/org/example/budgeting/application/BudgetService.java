package org.example.budgeting.application;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.application.dtos.BudgetDto;
import org.example.budgeting.application.dtos.BudgetQuote;
import org.example.budgeting.application.dtos.ReservationRequest;
import org.example.budgeting.domain.enums.BudgetStatus;
import org.example.budgeting.domain.model.Budget;
import org.example.budgeting.domain.service.feasibility.BudgetFeasibilityService;
import org.example.budgeting.domain.service.feasibility.FeasibilityResult;
import org.example.budgeting.domain.service.pricing.BudgetPricingService;
import org.example.budgeting.domain.service.pricing.ProfitRange;
import org.example.budgeting.domain.service.reservation.ReservationCalculator;
import org.example.budgeting.infrastructure.client.CatalogClient;
import org.example.budgeting.infrastructure.client.InventoryClient;
import org.example.budgeting.infrastructure.client.MaterialView;
import org.example.budgeting.infrastructure.client.ProductView;
import org.example.budgeting.infrastructure.repositories.BudgetRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CatalogClient catalogClient;
    private final InventoryClient inventoryClient;
    private final BudgetFeasibilityService feasibilityService;
    private final BudgetPricingService pricingService;
    private final ReservationCalculator reservationCalculator;

    public List<BudgetDto> findAll() {
        return budgetRepository.findAll().stream().map(this::toDto).toList(); // Pergunta burra, como o stream funciona?
    }

    public Optional<BudgetDto> findById(UUID id) {
        return budgetRepository.findById(id).map(this::toDto);
    }

    public void deleteById(UUID id) {
        budgetRepository.deleteById(id);
    }

    public BudgetQuote createQuote(UUID productId, List<UUID> materialIds) {
        ProductView product = catalogClient.getProduct(productId);

        FeasibilityResult feasibility = feasibilityService.checkFeasibility(product, materialIds);
        if (!feasibility.feasible()) {
            throw new IllegalStateException("Orçamento inviável: " + feasibility.reason());
        }

        List<ProfitRange> ranges = pricingService.calculateProfitRanges(product);

        Budget budget = Budget.builder()
                .productId(productId)
                .materialIds(new LinkedHashSet<>(materialIds))
                .status(BudgetStatus.IN_VALIDATION)
                .build();
        budget = budgetRepository.save(budget);

        return new BudgetQuote(toDto(budget), ranges);
    }

    public void acceptBudget(UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));
        budget.confirm();

        ProductView product = catalogClient.getProduct(budget.getProductId());
        ReservationRequest request = reservationCalculator.buildReserve(budget.getId(), product);
        inventoryClient.reserve(request); // se estoque insuficiente, lança e o aceite é abortado

        budgetRepository.save(budget);
    }

    public void cancelBudget(UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));
        boolean wasInProgress = budget.getStatus() == BudgetStatus.IN_PROGRESS;
        budget.cancel();

        if (wasInProgress) {
            ProductView product = catalogClient.getProduct(budget.getProductId());
            ReservationRequest request = reservationCalculator.buildRelease(budget.getId(), product);
            inventoryClient.release(request);
        }

        budgetRepository.save(budget);
    }

    private BudgetDto toDto(Budget budget) {
        ProductView product = catalogClient.getProduct(budget.getProductId());
        List<MaterialView> materials = catalogClient.getMaterials(new ArrayList<>(budget.getMaterialIds()));
        return new BudgetDto(budget.getId(), product, materials, budget.getStatus());
    }
}
