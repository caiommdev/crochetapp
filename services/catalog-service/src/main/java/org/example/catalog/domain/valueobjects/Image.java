package org.example.catalog.domain.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.example.catalog.domain.enums.StorageType;

@Embeddable
@Getter
@Setter
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
