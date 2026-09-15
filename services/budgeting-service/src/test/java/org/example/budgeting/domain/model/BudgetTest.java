package org.example.budgeting.domain.model;

import org.example.budgeting.domain.enums.BudgetStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BudgetTest {

    @Test
    void startReserving_movesFromInValidationToReserving() {
        Budget budget = Budget.builder().status(BudgetStatus.IN_VALIDATION).build();

        budget.startReserving();

        assertThat(budget.getStatus()).isEqualTo(BudgetStatus.RESERVING);
    }

    @Test
    void startReserving_throwsWhenNotInValidation() {
        Budget budget = Budget.builder().status(BudgetStatus.RESERVING).build();

        assertThatThrownBy(budget::startReserving).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void confirm_movesFromReservingToInProgress() {
        Budget budget = Budget.builder().status(BudgetStatus.RESERVING).build();

        budget.confirm();

        assertThat(budget.getStatus()).isEqualTo(BudgetStatus.IN_PROGRESS);
    }

    @Test
    void confirm_throwsWhenNotReserving() {
        Budget budget = Budget.builder().status(BudgetStatus.IN_VALIDATION).build();

        assertThatThrownBy(budget::confirm).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void failReservation_movesFromReservingToCanceled() {
        Budget budget = Budget.builder().status(BudgetStatus.RESERVING).build();

        budget.failReservation();

        assertThat(budget.getStatus()).isEqualTo(BudgetStatus.CANCELED);
    }

    @Test
    void failReservation_throwsWhenNotReserving() {
        Budget budget = Budget.builder().status(BudgetStatus.IN_PROGRESS).build();

        assertThatThrownBy(budget::failReservation).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancel_worksFromAnyNonCanceledStatus() {
        Budget budget = Budget.builder().status(BudgetStatus.IN_PROGRESS).build();

        budget.cancel();

        assertThat(budget.getStatus()).isEqualTo(BudgetStatus.CANCELED);
    }

    @Test
    void cancel_throwsWhenAlreadyCanceled() {
        Budget budget = Budget.builder().status(BudgetStatus.CANCELED).build();

        assertThatThrownBy(budget::cancel).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void complete_movesFromInProgressToDone() {
        Budget budget = Budget.builder().status(BudgetStatus.IN_PROGRESS).build();

        budget.complete();

        assertThat(budget.getStatus()).isEqualTo(BudgetStatus.DONE);
    }

    @Test
    void complete_throwsWhenNotInProgress() {
        Budget budget = Budget.builder().status(BudgetStatus.RESERVING).build();

        assertThatThrownBy(budget::complete).isInstanceOf(IllegalStateException.class);
    }
}
