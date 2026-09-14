package org.example.catalog.domain.model;

import lombok.*;
import org.example.catalog.domain.enums.MaterialType;
import org.example.catalog.domain.valueobjects.Image;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialDefinition {
    private UUID id;
    private String name;
    private BigDecimal price;
    private MaterialType type;
    private Image image;
    private String color;
    private Integer metersPerSkein;
}
