package org.example.catalog.infrastructure.persistence.mappers;

import org.example.catalog.domain.valueobjects.Image;
import org.example.catalog.infrastructure.persistence.entities.ImageEmbeddable;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    public Image toDomain(ImageEmbeddable embeddable) {
        if (embeddable == null) return null;
        return new Image(embeddable.getName(), embeddable.getPath(), embeddable.getStorage());
    }

    public ImageEmbeddable toEmbeddable(Image domain) {
        if (domain == null) return null;
        return ImageEmbeddable.builder()
                .name(domain.name())
                .path(domain.path())
                .storage(domain.storage())
                .build();
    }
}
