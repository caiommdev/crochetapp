package org.example.budgeting.domain.service.feasibility;

import org.example.budgeting.infrastructure.client.ProductView;
import org.example.budgeting.infrastructure.client.RequirementView;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BudgetFeasibilityService {
    public FeasibilityResult checkFeasibility(ProductView product, Collection<UUID> selectedMaterialIds) {
        Set<UUID> selected = selectedMaterialIds == null ? Set.of() : selectedMaterialIds.stream().collect(Collectors.toSet());
        for (RequirementView req : product.recipe().materialRequirements()) {
            UUID required = req.material() != null ? req.material().id() : null;
            if (required == null || !selected.contains(required)) {
                String name = req.material() != null ? req.material().name() : String.valueOf(required);
                return FeasibilityResult.isNotFeasible("Material not available: " + name);
            }
        }
        return FeasibilityResult.isFeasible();
    }
}
