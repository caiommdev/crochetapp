package org.example.crochetapp.domain.service.pricing;

import java.math.BigDecimal;

public record ProfitRange(
        String label,
        BigDecimal cost,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal minProfit,
        BigDecimal maxProfit
) {}
