package org.example.crochetapp.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.example.crochetapp.domain.model.enums.StorageType;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Image {

    @Column(name = "image_name")
    private String name;

    @Column(name = "image_path")
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_storage")
    private StorageType storage;
}
