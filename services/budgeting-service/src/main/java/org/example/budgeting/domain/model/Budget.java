package org.example.budgeting.domain.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "budget_materials", joinColumns = @JoinColumn(name = "budget_id"))
    @Column(name = "material_id")
    @Builder.Default
    private Set<UUID> materialIds = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
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
