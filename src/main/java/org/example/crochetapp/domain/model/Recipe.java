package org.example.crochetapp.domain.model;

import lombok.*;
import org.example.crochetapp.domain.model.valueobjects.Image;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {
    int id;
    String name;
    String description;
    //TODO: all points gona change to a enum but first we need to map all points
    Map<String, Integer> points = new HashMap<>();
    Image image;
}
