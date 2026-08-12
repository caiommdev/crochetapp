package org.example.budgeting.domain.service.reservation;

import org.example.budgeting.application.dtos.ReservationRequest;
import org.example.budgeting.infrastructure.client.MaterialType;
import org.example.budgeting.infrastructure.client.MaterialView;
import org.example.budgeting.infrastructure.client.PointView;
import org.example.budgeting.infrastructure.client.ProductView;
import org.example.budgeting.infrastructure.client.RequirementView;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Calcula, a partir da receita, quanto abater (reserva) ou devolver (liberação) de cada material.
 * O resultado são linhas prontas que o Inventory Service apenas aplica — mantendo o Inventory
 * como um "livro-razão" de estoque, sem conhecer receitas.
 */
@Service
public class ReservationCalculator {

    public ReservationRequest buildReserve(UUID budgetId, ProductView product) {
        return build(budgetId, product, true);
    }

    public ReservationRequest buildRelease(UUID budgetId, ProductView product) {
        return build(budgetId, product, false);
    }

    private ReservationRequest build(UUID budgetId, ProductView product, boolean reserve) {
        BigDecimal totalMeters = calculateTotalMeters(product.recipe().points());
        List<ReservationRequest.ReservationLine> lines = new ArrayList<>();

        for (RequirementView req : product.recipe().materialRequirements()) {
            MaterialView material = req.material();
            Integer quantity = null;
            Integer meters = null;

            switch (material.type()) {
                case YARN -> {
                    if (material.meters() != null && material.meters() > 0) {
                        RoundingMode mode = reserve ? RoundingMode.CEILING : RoundingMode.FLOOR;
                        quantity = totalMeters.divide(BigDecimal.valueOf(material.meters()), 0, mode).intValue();
                    } else {
                        quantity = req.quantityNeeded();
                    }
                }
                case METER_ACCESSORY -> {
                    RoundingMode mode = reserve ? RoundingMode.CEILING : RoundingMode.FLOOR;
                    meters = totalMeters.setScale(0, mode).intValue();
                }
                case ACCESSORY -> quantity = req.quantityNeeded();
            }

            lines.add(new ReservationRequest.ReservationLine(material.id(), quantity, meters));
        }

        return new ReservationRequest(budgetId, lines);
    }

    private BigDecimal calculateTotalMeters(List<PointView> points) {
        int totalCentimeters = points.stream()
                .mapToInt(PointView::centimetersPerPoint)
                .sum();
        return BigDecimal.valueOf(totalCentimeters)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
    }
}
