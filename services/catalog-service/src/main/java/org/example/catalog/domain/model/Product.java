package org.example.catalog.domain.model;

import lombok.*;
import org.example.catalog.domain.valueobjects.Image;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private UUID id;
    private String name;
    private Recipe recipe;
    private Image image;
}