package org.example.catalog.infrastructure.persistence.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class PointEmbeddable {
    private String name;
    private Integer centimetersPerPoint;
    private Integer quantity;
}
