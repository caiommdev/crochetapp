package org.example.catalog.application;

import org.example.catalog.api.dto.MaterialDto;
import org.example.catalog.domain.model.MaterialDefinition;
import org.example.catalog.infrastructure.client.StockView;
import org.example.catalog.infrastructure.client.StockWriteDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MaterialMapper {

    public MaterialDto toDto(MaterialDefinition def, StockView stock) {
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
        def.setColor(in.type() == org.example.catalog.domain.enums.MaterialType.YARN ? in.color() : null);
        def.setMetersPerSkein(in.type() == org.example.catalog.domain.enums.MaterialType.YARN ? in.meters() : null);
    }

    public StockWriteDto toStockWrite(UUID materialId, MaterialDto in) {
        Integer quantity = null;
        Integer meters = null;
        switch (in.type()) {
            case YARN, ACCESSORY -> quantity = in.quantity();
            case METER_ACCESSORY -> meters = in.meters();
        }
        return new StockWriteDto(materialId, quantity, meters);
    }
}
