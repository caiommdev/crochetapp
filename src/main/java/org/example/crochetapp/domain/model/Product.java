package org.example.crochetapp.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

import org.example.crochetapp.domain.model.material.Material;
import org.example.crochetapp.domain.model.valueobjects.Image;
import org.example.crochetapp.domain.model.valueobjects.RecipeMaterialRequirement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Embedded
    private Image image;

    public List<Material> getRequiredMaterials() {
        return recipe.getMaterialRequirements().stream()
                .map(RecipeMaterialRequirement::getMaterial)
                .toList();
    }
}
