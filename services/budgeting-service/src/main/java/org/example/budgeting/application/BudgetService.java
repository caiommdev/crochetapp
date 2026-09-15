package org.example.budgeting.application;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.budgeting.application.dtos.BudgetDto;
import org.example.budgeting.application.dtos.BudgetQuote;
import org.example.budgeting.application.dtos.ReservationRequest;
import org.example.budgeting.domain.enums.BudgetStatus;
import org.example.budgeting.domain.events.MaterialsReleaseRequested;
import org.example.budgeting.domain.events.MaterialsReservationRequested;
import org.example.budgeting.domain.model.Budget;
import org.example.budgeting.domain.repository.BudgetRepository;
import org.example.budgeting.domain.service.feasibility.BudgetFeasibilityService;
import org.example.budgeting.domain.service.feasibility.FeasibilityResult;
import org.example.budgeting.domain.service.pricing.BudgetPricingService;
import org.example.budgeting.domain.service.pricing.ProfitRange;
import org.example.budgeting.domain.service.reservation.ReservationCalculator;
import org.example.budgeting.domain.shared.DomainEventPublisher;
import org.example.budgeting.infrastructure.cache.MaterialReadModelStore;
import org.example.budgeting.infrastructure.cache.MaterialView;
import org.example.budgeting.infrastructure.cache.ProductReadModelStore;
import org.example.budgeting.infrastructure.cache.ProductView;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ProductReadModelStore productReadModelStore;
    private final MaterialReadModelStore materialReadModelStore;
    private final DomainEventPublisher eventPublisher;
    private final BudgetFeasibilityService feasibilityService;
    private final BudgetPricingService pricingService;
    private final ReservationCalculator reservationCalculator;

    public List<BudgetDto> findAll() {
        return budgetRepository.findAll().stream().map(this::toDto).toList();
    }

    public Optional<BudgetDto> findById(UUID id) {
        return budgetRepository.findById(id).map(this::toDto);
    }

    public void deleteById(UUID id) {
        budgetRepository.deleteById(id);
    }

    public BudgetQuote createQuote(UUID productId, List<UUID> materialIds) {
        ProductView product = findProduct(productId);

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
        budget.startReserving();
        budgetRepository.save(budget);

        ProductView product = findProduct(budget.getProductId());
        ReservationRequest request = reservationCalculator.buildReserve(budget.getId(), product);
        eventPublisher.publish(List.of(new MaterialsReservationRequested(request.budgetId(), toLines(request))));
    }

    public void applyReservationResult(UUID budgetId, boolean success, String reason) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));
        if (success) {
            budget.confirm();
        } else {
            budget.failReservation();
        }
        budgetRepository.save(budget);
    }

    public void cancelBudget(UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));
        boolean hadReservation = budget.getStatus() == BudgetStatus.IN_PROGRESS
                || budget.getStatus() == BudgetStatus.RESERVING;
        budget.cancel();
        budgetRepository.save(budget);

        if (hadReservation) {
            ProductView product = findProduct(budget.getProductId());
            ReservationRequest request = reservationCalculator.buildRelease(budget.getId(), product);
            eventPublisher.publish(List.of(new MaterialsReleaseRequested(request.budgetId(), toReleaseLines(request))));
        }
    }

    private BudgetDto toDto(Budget budget) {
        ProductView product = findProduct(budget.getProductId());
        List<MaterialView> materials = materialReadModelStore.findAllById(new ArrayList<>(budget.getMaterialIds()));
        return new BudgetDto(budget.getId(), product, materials, budget.getStatus());
    }

    private ProductView findProduct(UUID productId) {
        return productReadModelStore.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + productId));
    }

    private List<MaterialsReservationRequested.Line> toLines(ReservationRequest request) {
        return request.lines().stream()
                .map(line -> new MaterialsReservationRequested.Line(line.materialId(), line.quantity(), line.meters()))
                .toList();
    }

    private List<MaterialsReleaseRequested.Line> toReleaseLines(ReservationRequest request) {
        return request.lines().stream()
                .map(line -> new MaterialsReleaseRequested.Line(line.materialId(), line.quantity(), line.meters()))
                .toList();
    }
}
