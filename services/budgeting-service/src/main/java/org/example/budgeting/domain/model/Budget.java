package org.example.budgeting.domain.model;

import lombok.*;
import org.example.budgeting.domain.enums.BudgetStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    private UUID id;

    private UUID productId;

    @Builder.Default
    private Set<UUID> materialIds = new HashSet<>();

    private BudgetStatus status;

    public void confirm() {
        if (this.status != BudgetStatus.IN_VALIDATION)
            throw new IllegalStateException("Budget is already in validation.");
        this.status = BudgetStatus.IN_PROGRESS;
    }

    public void cancel() {
        if (this.status == BudgetStatus.CANCELED)
            throw new IllegalStateException("Budget is already canceled.");
        this.status = BudgetStatus.CANCELED;
    }

    public void complete() {
        if (this.status != BudgetStatus.IN_PROGRESS)
            throw new IllegalStateException("Budget is already completed.");
        this.status = BudgetStatus.DONE;
    }
}
