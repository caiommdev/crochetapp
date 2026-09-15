package org.example.catalog.application;

import org.example.catalog.api.dto.MaterialDto;
import org.example.catalog.domain.enums.MaterialType;
import org.example.catalog.domain.model.MaterialDefinition;
import org.example.catalog.infrastructure.cache.MaterialStockCache;
import org.springframework.stereotype.Component;

@Component
public class MaterialMapper {

    public record StockValues(Integer quantity, Integer meters) {}

    public MaterialDto toDto(MaterialDefinition def, MaterialStockCache.StockLevel stock) {
        Integer quantity = null;
        Integer meters = null;
        switch (def.getType()) {
            case YARN -> {
                quantity = stock != null ? stock.quantity() : null;
                meters = def.getMetersPerSkein();
            }
            case ACCESSORY -> quantity = stock != null ? stock.quantity() : null;
            case METER_ACCESSORY -> meters = stock != null ? stock.meters() : null;
        }
        return new MaterialDto(
                def.getId(), def.getName(), def.getType(), def.getPrice(),
                def.getImage(), def.getColor(), quantity, meters);
    }

    public void applyToDefinition(MaterialDto in, MaterialDefinition def) {
        def.setName(in.name());
        def.setPrice(in.price());
        def.setType(in.type());
        def.setImage(in.image());
        def.setColor(in.type() == MaterialType.YARN ? in.color() : null);
        def.setMetersPerSkein(in.type() == MaterialType.YARN ? in.meters() : null);
    }

    public StockValues resolveStock(MaterialDto in) {
        Integer quantity = null;
        Integer meters = null;
        switch (in.type()) {
            case YARN, ACCESSORY -> quantity = in.quantity();
            case METER_ACCESSORY -> meters = in.meters();
        }
        return new StockValues(quantity, meters);
    }
}
