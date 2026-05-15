package org.example.crochetapp.domain.model.valueobjects;

import org.example.crochetapp.domain.model.enums.StorageType;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Image {
    private String name;
    private String path;
    private StorageType storage;
}
