package org.example.budgeting.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RequirementView(
        MaterialView material,
        Integer quantityNeeded
) {}
