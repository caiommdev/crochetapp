package org.example.catalog.domain.valueobjects;

import java.util.UUID;

public record MaterialRequirement(UUID materialId, Integer quantityNeeded) {
}
