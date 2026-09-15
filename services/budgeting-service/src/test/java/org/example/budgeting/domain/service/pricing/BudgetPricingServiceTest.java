package org.example.budgeting.domain.service.pricing;

import org.example.budgeting.infrastructure.cache.MaterialType;
import org.example.budgeting.infrastructure.cache.MaterialView;
import org.example.budgeting.infrastructure.cache.PointView;
import org.example.budgeting.infrastructure.cache.ProductView;
import org.example.budgeting.infrastructure.cache.RecipeView;
import org.example.budgeting.infrastructure.cache.RequirementView;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetPricingServiceTest {

    private final BudgetPricingService service = new BudgetPricingService();

    @Test
    void calculateProfitRanges_singleYarnMaterialWithAmpleStock() {
        // receita precisa de 5m (um ponto de 500cm); fio custa 10 o novelo, com 5m por novelo => 2/m => custo 10
        MaterialView material = new MaterialView(UUID.randomUUID(), "Fio", MaterialType.YARN,
                new BigDecimal("10"), null, null, 10, 5);
        RequirementView requirement = new RequirementView(material, 1);
        RecipeView recipe = new RecipeView(UUID.randomUUID(), "Receita", null,
                List.of(new PointView("Ponto", 500, 1)), null, List.of(requirement));
        ProductView product = new ProductView(UUID.randomUUID(), "Produto", recipe, null);

        List<ProfitRange> ranges = service.calculateProfitRanges(product);

        assertThat(ranges).hasSize(3);
        ProfitRange conservadora = ranges.get(0);
        assertThat(conservadora.cost()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(conservadora.minPrice()).isEqualByComparingTo(new BigDecimal("13.00"));
        assertThat(conservadora.maxPrice()).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    void calculateProfitRanges_accessoryUsesFixedPricePerUnit() {
        MaterialView material = new MaterialView(UUID.randomUUID(), "Botão", MaterialType.ACCESSORY,
                new BigDecimal("3"), null, null, 100, null);
        RequirementView requirement = new RequirementView(material, 2);
        RecipeView recipe = new RecipeView(UUID.randomUUID(), "Receita", null, List.of(), null, List.of(requirement));
        ProductView product = new ProductView(UUID.randomUUID(), "Produto", recipe, null);

        List<ProfitRange> ranges = service.calculateProfitRanges(product);

        assertThat(ranges.get(0).cost()).isEqualByComparingTo(new BigDecimal("6.00"));
    }
}
