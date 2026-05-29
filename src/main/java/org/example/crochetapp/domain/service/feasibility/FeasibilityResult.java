package org.example.crochetapp.domain.service.feasibility;

public record FeasibilityResult(
    boolean feasible,
    String reason
) {
    public static FeasibilityResult isFeasible() {
        return new FeasibilityResult(true, null);
    }

    public static FeasibilityResult isNotFeasible(String reason) {
        return new FeasibilityResult(false, reason);
    }
}
