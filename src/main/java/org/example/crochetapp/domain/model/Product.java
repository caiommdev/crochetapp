package org.example.crochetapp.domain.model;

import lombok.*;
import org.example.crochetapp.domain.model.valueobjects.Image;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    int id;
    String name;
    Recipe recipe;
    Image image;
}
