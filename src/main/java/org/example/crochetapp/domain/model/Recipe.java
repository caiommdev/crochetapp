package org.example.crochetapp.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.crochetapp.domain.model.valueobjects.Image;
import org.example.crochetapp.domain.model.valueobjects.Point;
import org.example.crochetapp.domain.model.valueobjects.RecipeMaterialRequirement;

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
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "recipe_points",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @Builder.Default
    private List<Point> points = new ArrayList<>();

    @Embedded
    private Image image;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "recipe_material_requirements",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @Builder.Default
    private List<RecipeMaterialRequirement> materialRequirements = new ArrayList<>();
}
