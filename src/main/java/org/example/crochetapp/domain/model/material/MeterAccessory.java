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
@DiscriminatorValue("METER_ACCESSORY")
public class MeterAccessory extends Material {

    @Column(name = "meters")
    private Integer meters;
}
