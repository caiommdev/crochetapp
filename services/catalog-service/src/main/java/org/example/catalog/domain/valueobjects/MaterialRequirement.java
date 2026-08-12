package org.example.catalog.domain.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

/**
 * Requisito de material de uma receita. Referencia o material apenas por ID
 * (o material é dono do Catalog, mas aqui guardamos só o identificador + quantidade).
 */
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class MaterialRequirement {

    @Column(name = "material_id")
    private UUID materialId;

    @Column(name = "quantity_needed")
    private Integer quantityNeeded;
}
