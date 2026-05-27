package org.example.crochetapp.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.crochetapp.domain.model.valueobjects.Image;

import java.util.HashMap;
import java.util.Map;
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

    // TODO: trocar a chave String por um enum quando o conjunto de pontos estiver mapeado.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "recipe_points",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @MapKeyColumn(name = "point_name")
    @Column(name = "point_value")
    @Builder.Default
    private Map<String, Integer> points = new HashMap<>();

    @Embedded
    private Image image;
}
