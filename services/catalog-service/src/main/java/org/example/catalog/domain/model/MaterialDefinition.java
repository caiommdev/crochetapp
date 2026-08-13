package org.example.catalog.domain.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "materials")
public class MaterialDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    private MaterialType type;

    @Embedded
    private Image image;

    @Column(name = "color")
    private String color;

    @Column(name = "meters_per_skein")
    private Integer metersPerSkein;
}
