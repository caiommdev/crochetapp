package org.example.budgeting.domain.service.pricing;

import org.example.budgeting.infrastructure.client.MaterialType;
import org.example.budgeting.infrastructure.client.MaterialView;
import org.example.budgeting.infrastructure.client.PointView;
import org.example.budgeting.infrastructure.client.ProductView;
import org.example.budgeting.infrastructure.client.RequirementView;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class BudgetPricingService {
    public List<ProfitRange> calculateProfitRanges(ProductView product) {
        List<RequirementView> requirements = product.recipe().materialRequirements();
        List<PointView> recipePoints = product.recipe().points();

        BigDecimal totalMeters = BigDecimal.valueOf(
                recipePoints.stream().mapToInt(PointView::centimetersPerPoint).sum()
        ).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        BigDecimal totalCost = calculateTotalCost(requirements, totalMeters);

        return List.of(
                buildRange("Conservadora (30% a 50%)", totalCost, new BigDecimal("1.30"), new BigDecimal("1.50")),
                buildRange("Equilibrada (50% a 100%)", totalCost, new BigDecimal("1.50"), new BigDecimal("2.00")),
                buildRange("Premium (100% a 200%+)", totalCost, new BigDecimal("2.00"), new BigDecimal("3.00"))
        );
    }

    private BigDecimal calculateTotalCost(List<RequirementView> requirements, BigDecimal totalMeters) {
        List<RequirementView> meteredReqs = requirements.stream()
                .filter(this::isMetered)
                .toList();

        BigDecimal meteredCost = calculateMeteredCost(meteredReqs, totalMeters);

        BigDecimal fixedCost = requirements.stream()
                .filter(r -> !isMetered(r))
                .map(r -> r.material().price().multiply(BigDecimal.valueOf(r.quantityNeeded())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return meteredCost.add(fixedCost);
    }

    private boolean isMetered(RequirementView r) {
        MaterialView m = r.material();
        if (m.type() == MaterialType.YARN) {
            return m.meters() != null && m.meters() > 0;
        }
        return m.type() == MaterialType.METER_ACCESSORY;
    }

    private BigDecimal calculateMeteredCost(List<RequirementView> reqs, BigDecimal totalMeters) {
        int n = reqs.size();
        if (n == 0) return BigDecimal.ZERO;

        BigDecimal[] allocated = new BigDecimal[n];
        BigDecimal[] capacity = new BigDecimal[n];
        BigDecimal[] pricePerMeter = new BigDecimal[n];
        java.util.Arrays.fill(allocated, BigDecimal.ZERO);

        for (int i = 0; i < n; i++) {
            MaterialView m = reqs.get(i).material();
            if (m.type() == MaterialType.YARN) {
                capacity[i] = BigDecimal.valueOf((long) safe(m.quantity()) * m.meters());
                pricePerMeter[i] = m.price().divide(BigDecimal.valueOf(m.meters()), 10, RoundingMode.HALF_UP);
            } else {
                capacity[i] = BigDecimal.valueOf(safe(m.meters()));
                pricePerMeter[i] = m.price().divide(BigDecimal.valueOf(m.meters()), 10, RoundingMode.HALF_UP);
            }
        }

        boolean[] saturated = new boolean[n];
        BigDecimal remaining = totalMeters;

        while (remaining.compareTo(BigDecimal.ZERO) > 0) {
            long activeCount = 0;
            for (boolean s : saturated) if (!s) activeCount++;
            if (activeCount == 0) break;

            BigDecimal share = remaining.divide(BigDecimal.valueOf(activeCount), 10, RoundingMode.HALF_UP);
            boolean anySaturated = false;
            BigDecimal distributed = BigDecimal.ZERO;

            for (int i = 0; i < n; i++) {
                if (saturated[i]) continue;
                BigDecimal available = capacity[i].subtract(allocated[i]);
                BigDecimal take = share.min(available);
                allocated[i] = allocated[i].add(take);
                distributed = distributed.add(take);
                if (available.compareTo(share) <= 0) {
                    saturated[i] = true;
                    anySaturated = true;
                }
            }
            remaining = remaining.subtract(distributed);
            if (!anySaturated) break;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < n; i++) {
            total = total.add(pricePerMeter[i].multiply(allocated[i]));
        }
        return total;
    }

    private int safe(Integer value) {
        return value == null ? 0 : value;
    }

    private ProfitRange buildRange(String label, BigDecimal cost,
                                   BigDecimal minMultiplier, BigDecimal maxMultiplier) {
        BigDecimal minPrice = cost.multiply(minMultiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxPrice = cost.multiply(maxMultiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal minProfit = minPrice.subtract(cost).setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxProfit = maxPrice.subtract(cost).setScale(2, RoundingMode.HALF_UP);
        return new ProfitRange(label, cost.setScale(2, RoundingMode.HALF_UP), minPrice, maxPrice, minProfit, maxProfit);
    }
}
