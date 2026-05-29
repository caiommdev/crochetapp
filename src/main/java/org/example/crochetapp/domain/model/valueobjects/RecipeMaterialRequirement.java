package org.example.crochetapp.domain.model.valueobjects;

import java.util.UUID;

import org.example.crochetapp.domain.model.material.Material;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class RecipeMaterialRequirement {
    @ManyToOne
    private Material material;
    private Integer quantityNeeded;
}