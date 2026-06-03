package org.example.crochetapp.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.example.crochetapp.domain.model.Budget;
import org.example.crochetapp.domain.model.Recipe;
import org.example.crochetapp.domain.model.material.Accessory;
import org.example.crochetapp.domain.model.material.MeterAccessory;
import org.example.crochetapp.domain.model.material.Yarn;
import org.example.crochetapp.domain.model.valueobjects.Point;
import org.example.crochetapp.domain.model.valueobjects.RecipeMaterialRequirement;
import org.springframework.stereotype.Service;

@Service
public class MaterialReservationService {

    public void reserve(Budget budget) {
        Recipe recipe = budget.getProduct().getRecipe();
        BigDecimal totalMeters = calculateTotalMeters(recipe.getPoints());

        for (RecipeMaterialRequirement req : recipe.getMaterialRequirements())
            deductStock(req, totalMeters);
    }

    public void release(Budget budget) {
        Recipe recipe = budget.getProduct().getRecipe();
        BigDecimal totalMeters = calculateTotalMeters(recipe.getPoints());

        for (RecipeMaterialRequirement req : recipe.getMaterialRequirements()) {
            restoreStock(req, totalMeters);
        }
    }

    private BigDecimal calculateTotalMeters(List<Point> points) {
        int totalCentimeters = points.stream()
                .mapToInt(Point::getCentimetersPerPoint)
                .sum();
        return BigDecimal.valueOf(totalCentimeters)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
    }

    private void deductStock(RecipeMaterialRequirement req, BigDecimal totalMeters) {
        var material = req.getMaterial();
        if (material instanceof Yarn yarn) {
            if (yarn.getMeters() != null && yarn.getMeters() > 0) {
                int skeinsNeeded = totalMeters
                        .divide(BigDecimal.valueOf(yarn.getMeters()), 0, RoundingMode.CEILING)
                        .intValue();
                if (yarn.getQuantity() < skeinsNeeded) {
                    throw new IllegalStateException(
                            "Insufficient stock of '" + yarn.getName() + "'. Required: "
                            + skeinsNeeded + " skein(s), available: " + yarn.getQuantity());
                }
                yarn.setQuantity(yarn.getQuantity() - skeinsNeeded);
            } else {
                int needed = req.getQuantityNeeded();
                if (yarn.getQuantity() < needed) {
                    throw new IllegalStateException(
                            "Insufficient stock of '" + yarn.getName() + "'. Required: "
                            + needed + " unit(s), available: " + yarn.getQuantity());
                }
                yarn.setQuantity(yarn.getQuantity() - needed);
            }
        } else if (material instanceof MeterAccessory meterAcc) {
            int metersNeeded = totalMeters.setScale(0, RoundingMode.CEILING).intValue();
            if (meterAcc.getMeters() < metersNeeded) {
                throw new IllegalStateException(
                        "Insufficient stock of '" + meterAcc.getName() + "'. Required: "
                        + metersNeeded + "m, available: " + meterAcc.getMeters() + "m");
            }
            meterAcc.setMeters(meterAcc.getMeters() - metersNeeded);
        } else if (material instanceof Accessory accessory) {
            if (accessory.getQuantity() < req.getQuantityNeeded()) {
                throw new IllegalStateException(
                        "Insufficient stock of '" + accessory.getName() + "'. Required: "
                        + req.getQuantityNeeded() + " unit(s), available: " + accessory.getQuantity());
            }
            accessory.setQuantity(accessory.getQuantity() - req.getQuantityNeeded());
        }
    }

    private void restoreStock(RecipeMaterialRequirement req, BigDecimal totalMeters) {
        var material = req.getMaterial();
        if (material instanceof Yarn yarn) {
            if (yarn.getMeters() != null && yarn.getMeters() > 0) {
                int skeinsReturned = totalMeters
                        .divide(BigDecimal.valueOf(yarn.getMeters()), 0, RoundingMode.FLOOR)
                        .intValue();
                yarn.setQuantity(yarn.getQuantity() + skeinsReturned);
            } else {
                yarn.setQuantity(yarn.getQuantity() + req.getQuantityNeeded());
            }
        } else if (material instanceof MeterAccessory meterAcc) {
            int metersReturned = totalMeters.setScale(0, RoundingMode.FLOOR).intValue();
            meterAcc.setMeters(meterAcc.getMeters() + metersReturned);
        } else if (material instanceof Accessory accessory) {
            accessory.setQuantity(accessory.getQuantity() + req.getQuantityNeeded());
        }
    }
}
