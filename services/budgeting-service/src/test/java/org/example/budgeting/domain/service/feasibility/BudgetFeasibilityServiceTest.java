package org.example.budgeting.domain.service.feasibility;

import org.example.budgeting.infrastructure.cache.MaterialType;
import org.example.budgeting.infrastructure.cache.MaterialView;
import org.example.budgeting.infrastructure.cache.ProductView;
import org.example.budgeting.infrastructure.cache.RecipeView;
import org.example.budgeting.infrastructure.cache.RequirementView;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetFeasibilityServiceTest {

    private final BudgetFeasibilityService service = new BudgetFeasibilityService();

    @Test
    void checkFeasibility_feasibleWhenAllRequiredMaterialsSelected() {
        UUID materialId = UUID.randomUUID();
        ProductView product = productWithRequirement(materialId);

        FeasibilityResult result = service.checkFeasibility(product, List.of(materialId));

        assertThat(result.feasible()).isTrue();
    }

    @Test
    void checkFeasibility_notFeasibleWhenRequiredMaterialMissing() {
        UUID materialId = UUID.randomUUID();
        ProductView product = productWithRequirement(materialId);

        FeasibilityResult result = service.checkFeasibility(product, List.of());

        assertThat(result.feasible()).isFalse();
        assertThat(result.reason()).contains("Material not available");
    }

    private ProductView productWithRequirement(UUID materialId) {
        MaterialView material = new MaterialView(materialId, "Botão", MaterialType.ACCESSORY, BigDecimal.ONE, null, null, 10, null);
        RequirementView requirement = new RequirementView(material, 2);
        RecipeView recipe = new RecipeView(UUID.randomUUID(), "Receita", null, List.of(), null, List.of(requirement));
        return new ProductView(UUID.randomUUID(), "Produto", recipe, null);
    }
}
