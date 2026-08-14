package org.example.budgeting.application.dtos;

import org.example.budgeting.domain.service.pricing.ProfitRange;

import java.util.List;

public record BudgetQuote(
        BudgetDto budget,
        List<ProfitRange> profitRanges
) {}
