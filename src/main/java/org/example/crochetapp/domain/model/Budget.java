package org.example.crochetapp.domain.model;

import lombok.*;
import org.example.crochetapp.domain.model.enums.BudgetStatus;
import org.example.crochetapp.domain.model.material.Material;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {
    int id;
    Product product;
    Collection<Material> materials;
    BudgetStatus status;
}
