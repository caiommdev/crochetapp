package org.example.catalog.domain.model;

import lombok.*;
import org.example.catalog.domain.valueobjects.Image;
import org.example.catalog.domain.valueobjects.MaterialRequirement;
import org.example.catalog.domain.valueobjects.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    private UUID id;
    private String name;
    private String description;

    @Builder.Default
    private List<Point> points = new ArrayList<>();

    private Image image;

    @Builder.Default
    private List<MaterialRequirement> materialRequirements = new ArrayList<>();
}
