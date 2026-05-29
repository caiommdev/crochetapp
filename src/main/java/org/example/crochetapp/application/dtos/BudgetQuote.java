package org.example.crochetapp.application.dtos;

import java.util.List;

import org.example.crochetapp.domain.model.Budget;
import org.example.crochetapp.domain.service.pricing.ProfitRange;

public record BudgetQuote (
    Budget budget,
    List<ProfitRange> profitRanges
) {}
