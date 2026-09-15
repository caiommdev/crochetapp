package org.example.budgeting.application;

import org.example.budgeting.application.dtos.BudgetQuote;
import org.example.budgeting.application.dtos.ReservationRequest;
import org.example.budgeting.domain.enums.BudgetStatus;
import org.example.budgeting.domain.events.MaterialsConsumed;
import org.example.budgeting.domain.events.MaterialsReservationRequested;
import org.example.budgeting.domain.model.Budget;
import org.example.budgeting.domain.repository.BudgetRepository;
import org.example.budgeting.domain.service.feasibility.BudgetFeasibilityService;
import org.example.budgeting.domain.service.feasibility.FeasibilityResult;
import org.example.budgeting.domain.service.pricing.BudgetPricingService;
import org.example.budgeting.domain.service.reservation.ReservationCalculator;
import org.example.budgeting.domain.shared.DomainEventPublisher;
import org.example.budgeting.infrastructure.cache.MaterialReadModelStore;
import org.example.budgeting.infrastructure.cache.ProductReadModelStore;
import org.example.budgeting.infrastructure.cache.ProductView;
import org.example.budgeting.infrastructure.cache.RecipeView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private ProductReadModelStore productReadModelStore;
    @Mock
    private MaterialReadModelStore materialReadModelStore;
    @Mock
    private DomainEventPublisher eventPublisher;
    @Mock
    private BudgetFeasibilityService feasibilityService;
    @Mock
    private BudgetPricingService pricingService;
    @Mock
    private ReservationCalculator reservationCalculator;

    @InjectMocks
    private BudgetService budgetService;

    private ProductView product(UUID productId) {
        RecipeView recipe = new RecipeView(UUID.randomUUID(), "Receita", null, List.of(), null, List.of());
        return new ProductView(productId, "Produto", recipe, null);
    }

    @Test
    void createQuote_throwsWhenNotFeasible() {
        UUID productId = UUID.randomUUID();
        when(productReadModelStore.findById(productId)).thenReturn(Optional.of(product(productId)));
        when(feasibilityService.checkFeasibility(any(), any())).thenReturn(FeasibilityResult.isNotFeasible("faltou algo"));

        assertThatThrownBy(() -> budgetService.createQuote(productId, List.of()))
                .isInstanceOf(IllegalStateException.class);

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void createQuote_savesBudgetInValidationWhenFeasible() {
        UUID productId = UUID.randomUUID();
        ProductView productView = product(productId);
        when(productReadModelStore.findById(productId)).thenReturn(Optional.of(productView));
        when(feasibilityService.checkFeasibility(any(), any())).thenReturn(FeasibilityResult.isFeasible());
        when(pricingService.calculateProfitRanges(productView)).thenReturn(List.of());
        when(budgetRepository.save(any(Budget.class))).thenAnswer(invocation -> {
            Budget budget = invocation.getArgument(0);
            budget.setId(UUID.randomUUID());
            return budget;
        });
        when(materialReadModelStore.findAllById(any())).thenReturn(List.of());

        BudgetQuote quote = budgetService.createQuote(productId, List.of());

        assertThat(quote.budget().status()).isEqualTo(BudgetStatus.IN_VALIDATION);
    }

    @Test
    void acceptBudget_movesToReservingAndPublishesReservationRequested() {
        UUID budgetId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Budget budget = Budget.builder().id(budgetId).productId(productId).status(BudgetStatus.IN_VALIDATION).build();
        ProductView productView = product(productId);
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(productReadModelStore.findById(productId)).thenReturn(Optional.of(productView));
        when(reservationCalculator.buildReserve(budgetId, productView))
                .thenReturn(new ReservationRequest(budgetId, List.of()));

        budgetService.acceptBudget(budgetId);

        assertThat(budget.getStatus()).isEqualTo(BudgetStatus.RESERVING);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<MaterialsReservationRequested>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().get(0).budgetId()).isEqualTo(budgetId);
    }

    @Test
    void applyReservationResult_confirmsBudgetOnSuccess() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = Budget.builder().id(budgetId).status(BudgetStatus.RESERVING).build();
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        budgetService.applyReservationResult(budgetId, true, null);

        assertThat(budget.getStatus()).isEqualTo(BudgetStatus.IN_PROGRESS);
    }

    @Test
    void applyReservationResult_cancelsBudgetOnFailure() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = Budget.builder().id(budgetId).status(BudgetStatus.RESERVING).build();
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        budgetService.applyReservationResult(budgetId, false, "sem estoque");

        assertThat(budget.getStatus()).isEqualTo(BudgetStatus.CANCELED);
    }

    @Test
    void cancelBudget_releasesMaterialsWhenWasReserving() {
        UUID budgetId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Budget budget = Budget.builder().id(budgetId).productId(productId).status(BudgetStatus.RESERVING).build();
        ProductView productView = product(productId);
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(productReadModelStore.findById(productId)).thenReturn(Optional.of(productView));
        when(reservationCalculator.buildRelease(budgetId, productView))
                .thenReturn(new ReservationRequest(budgetId, List.of()));

        budgetService.cancelBudget(budgetId);

        assertThat(budget.getStatus()).isEqualTo(BudgetStatus.CANCELED);
        verify(eventPublisher).publish(any());
    }

    @Test
    void cancelBudget_doesNotPublishReleaseWhenStillInValidation() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = Budget.builder().id(budgetId).status(BudgetStatus.IN_VALIDATION).build();
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        budgetService.cancelBudget(budgetId);

        assertThat(budget.getStatus()).isEqualTo(BudgetStatus.CANCELED);
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void completeBudget_movesToDoneAndPublishesMaterialsConsumed() {
        UUID budgetId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Budget budget = Budget.builder().id(budgetId).productId(productId).status(BudgetStatus.IN_PROGRESS).build();
        ProductView productView = product(productId);
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(productReadModelStore.findById(productId)).thenReturn(Optional.of(productView));
        when(reservationCalculator.buildReserve(budgetId, productView))
                .thenReturn(new ReservationRequest(budgetId, List.of()));

        budgetService.completeBudget(budgetId);

        assertThat(budget.getStatus()).isEqualTo(BudgetStatus.DONE);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<MaterialsConsumed>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().get(0).budgetId()).isEqualTo(budgetId);
    }
}
