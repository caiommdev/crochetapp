package org.example.catalog.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class MaterialRequirementEmbeddable {

    @Column(name = "material_id")
    private UUID materialId;

    @Column(name = "quantity_needed")
    private Integer quantityNeeded;
}
