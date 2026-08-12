package org.example.catalog.domain.valueobjects;

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
public class Point {
    private String name;
    private Integer centimetersPerPoint;
    private Integer quantity;
}
