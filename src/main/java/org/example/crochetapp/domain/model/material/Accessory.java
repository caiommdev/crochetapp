package org.example.crochetapp.domain.model.material;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ACCESSORY")
public class Accessory extends Material {

    @Column(name = "quantity")
    private Integer quantity;
}
