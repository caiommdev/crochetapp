package org.example.inventory.infrastructure.persistence.mappers;

import org.example.inventory.domain.model.StockItem;
import org.example.inventory.infrastructure.persistence.entities.StockItemEntity;
import org.springframework.stereotype.Component;

@Component
public class StockItemMapper {

    public StockItem toDomain(StockItemEntity entity) {
        if (entity == null) return null;
        return StockItem.builder()
                .materialId(entity.getMaterialId())
                .quantity(entity.getQuantity())
                .meters(entity.getMeters())
                .build();
    }

    public StockItemEntity toEntity(StockItem domain) {
        if (domain == null) return null;
        return StockItemEntity.builder()
                .materialId(domain.getMaterialId())
                .quantity(domain.getQuantity())
                .meters(domain.getMeters())
                .build();
    }
}
