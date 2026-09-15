package org.example.budgeting.infrastructure.cache;

import java.util.UUID;

public record RequirementRow(UUID materialId, Integer quantityNeeded) {}