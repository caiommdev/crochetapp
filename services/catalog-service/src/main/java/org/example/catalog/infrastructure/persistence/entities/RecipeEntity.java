package org.example.catalog.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "recipes")
public class RecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "recipe_points", joinColumns = @JoinColumn(name = "recipe_id"))
    @Builder.Default
    private List<PointEmbeddable> points = new ArrayList<>();

    @Embedded
    private ImageEmbeddable image;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "recipe_material_requirements", joinColumns = @JoinColumn(name = "recipe_id"))
    @Builder.Default
    private List<MaterialRequirementEmbeddable> materialRequirements = new ArrayList<>();
}
