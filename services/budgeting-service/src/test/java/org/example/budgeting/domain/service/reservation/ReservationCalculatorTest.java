package org.example.budgeting.domain.service.reservation;

import org.example.budgeting.application.dtos.ReservationRequest;
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

class ReservationCalculatorTest {

    private final ReservationCalculator calculator = new ReservationCalculator();

    @Test
    void buildReserve_yarnRoundsUpNumberOfSkeinsNeeded() {
        // 250cm num único ponto = 2,5m necessários; novelo tem 1m => precisa arredondar pra 3 novelos
        UUID materialId = UUID.randomUUID();
        MaterialView material = new MaterialView(materialId, "Fio", MaterialType.YARN, BigDecimal.ONE, null, null, 100, 1);
        RequirementView requirement = new RequirementView(material, 1);
        RecipeView recipe = new RecipeView(UUID.randomUUID(), "Receita", null,
                List.of(new PointView("Ponto", 250, 1)), null, List.of(requirement));
        ProductView product = new ProductView(UUID.randomUUID(), "Produto", recipe, null);

        ReservationRequest request = calculator.buildReserve(UUID.randomUUID(), product);

        assertThat(request.lines()).hasSize(1);
        assertThat(request.lines().get(0).materialId()).isEqualTo(materialId);
        assertThat(request.lines().get(0).quantity()).isEqualTo(3);
    }

    @Test
    void buildRelease_yarnRoundsDownNumberOfSkeins() {
        UUID materialId = UUID.randomUUID();
        MaterialView material = new MaterialView(materialId, "Fio", MaterialType.YARN, BigDecimal.ONE, null, null, 100, 1);
        RequirementView requirement = new RequirementView(material, 1);
        RecipeView recipe = new RecipeView(UUID.randomUUID(), "Receita", null,
                List.of(new PointView("Ponto", 250, 1)), null, List.of(requirement));
        ProductView product = new ProductView(UUID.randomUUID(), "Produto", recipe, null);

        ReservationRequest request = calculator.buildRelease(UUID.randomUUID(), product);

        assertThat(request.lines().get(0).quantity()).isEqualTo(2);
    }

    @Test
    void buildReserve_accessoryUsesQuantityNeededDirectly() {
        UUID materialId = UUID.randomUUID();
        MaterialView material = new MaterialView(materialId, "Botão", MaterialType.ACCESSORY, BigDecimal.ONE, null, null, 100, null);
        RequirementView requirement = new RequirementView(material, 4);
        RecipeView recipe = new RecipeView(UUID.randomUUID(), "Receita", null, List.of(), null, List.of(requirement));
        ProductView product = new ProductView(UUID.randomUUID(), "Produto", recipe, null);

        ReservationRequest request = calculator.buildReserve(UUID.randomUUID(), product);

        assertThat(request.lines().get(0).quantity()).isEqualTo(4);
        assertThat(request.lines().get(0).meters()).isNull();
    }
}
