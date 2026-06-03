package org.example.crochetapp.domain.service.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import org.example.crochetapp.domain.model.Product;
import org.example.crochetapp.domain.model.material.Material;
import org.example.crochetapp.domain.model.material.MeterAccessory;
import org.example.crochetapp.domain.model.material.Yarn;
import org.example.crochetapp.domain.model.valueobjects.Point;
import org.example.crochetapp.domain.model.valueobjects.RecipeMaterialRequirement;
import org.springframework.stereotype.Service;

@Service
public class BudgetPricingService {

    public List<ProfitRange> calculateProfitRanges(Product product, List<Material> materials) {
        List<RecipeMaterialRequirement> requirements = product.getRecipe().getMaterialRequirements();
        List<Point> recipePoints = product.getRecipe().getPoints();

        BigDecimal totalMeters = BigDecimal.valueOf(
                recipePoints.stream().mapToInt(Point::getCentimetersPerPoint).sum()
        ).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        System.out.println("Total meters needed: " + totalMeters);
        System.out.println("Requirements size: " + requirements.size());
        System.out.println("Points size: " + recipePoints.size());

        BigDecimal totalCost = calculateTotalCost(requirements, totalMeters);
        System.out.println("Total cost calculated: " + totalCost);
        return List.of(
                buildRange("Conservadora (30% a 50%)", totalCost, new BigDecimal("1.30"), new BigDecimal("1.50")),
                buildRange("Equilibrada (50% a 100%)", totalCost, new BigDecimal("1.50"), new BigDecimal("2.00")),
                buildRange("Premium (100% a 200%+)", totalCost, new BigDecimal("2.00"), new BigDecimal("3.00"))
        );
    }

    private BigDecimal calculateTotalCost(List<RecipeMaterialRequirement> requirements, BigDecimal totalMeters) {
        List<RecipeMaterialRequirement> meteredReqs = requirements.stream()
                .filter(r -> {
                    if (r.getMaterial() instanceof Yarn yarn) {
                        return yarn.getMeters() != null && yarn.getMeters() > 0;
                    }
                    return r.getMaterial() instanceof MeterAccessory;
                })
                .toList();

        BigDecimal meteredCost = calculateMeteredCost(meteredReqs, totalMeters);

        BigDecimal fixedCost = requirements.stream()
                .filter(r -> {
                    if (r.getMaterial() instanceof Yarn yarn) {
                        return yarn.getMeters() == null || yarn.getMeters() == 0;
                    }
                    return !(r.getMaterial() instanceof MeterAccessory);
                })
                .map(r -> r.getMaterial().getPrice().multiply(BigDecimal.valueOf(r.getQuantityNeeded())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("Fixed cost calculated: " + fixedCost);
        System.out.println("Total cost calculated: " + meteredCost.add(fixedCost));

        return meteredCost.add(fixedCost);
    }

    private BigDecimal calculateMeteredCost(List<RecipeMaterialRequirement> reqs, BigDecimal totalMeters) {
        int n = reqs.size();
        if (n == 0) return BigDecimal.ZERO;

        BigDecimal[] allocated = new BigDecimal[n];
        BigDecimal[] capacity = new BigDecimal[n];
        BigDecimal[] pricePerMeter = new BigDecimal[n];
        Arrays.fill(allocated, BigDecimal.ZERO);

        for (int i = 0; i < n; i++) {
            Material m = reqs.get(i).getMaterial();
            if (m instanceof Yarn yarn) {
                System.out.println("Yarn: " + yarn.getName() + ", Quantity: " + yarn.getQuantity() + ", Meters per skein: " + yarn.getMeters() + ", Price: " + yarn.getPrice());
                capacity[i] = BigDecimal.valueOf((long) yarn.getQuantity() * yarn.getMeters());
                pricePerMeter[i] = yarn.getPrice()
                        .divide(BigDecimal.valueOf(yarn.getMeters()), 10, RoundingMode.HALF_UP);
            } else {
                MeterAccessory ma = (MeterAccessory) m;
                capacity[i] = BigDecimal.valueOf(ma.getMeters());
                pricePerMeter[i] = ma.getPrice()
                        .divide(BigDecimal.valueOf(ma.getMeters()), 10, RoundingMode.HALF_UP);
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

    private ProfitRange buildRange(String label, BigDecimal cost,
                                   BigDecimal minMultiplier, BigDecimal maxMultiplier) {
        BigDecimal minPrice = cost.multiply(minMultiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxPrice = cost.multiply(maxMultiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal minProfit = minPrice.subtract(cost).setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxProfit = maxPrice.subtract(cost).setScale(2, RoundingMode.HALF_UP);
        return new ProfitRange(label, cost.setScale(2, RoundingMode.HALF_UP), minPrice, maxPrice, minProfit, maxProfit);
    }
}
