package org.example.catalog.application;

import org.example.catalog.api.dto.MaterialDto;
import org.example.catalog.domain.enums.MaterialType;
import org.example.catalog.domain.model.MaterialDefinition;
import org.example.catalog.infrastructure.cache.MaterialStockCache;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MaterialMapperTest {

    private final MaterialMapper mapper = new MaterialMapper();

    @Test
    void toDto_yarnUsesStockQuantityAndMetersPerSkeinAsMeters() {
        MaterialDefinition def = MaterialDefinition.builder()
                .id(UUID.randomUUID()).name("La Azul").type(MaterialType.YARN)
                .price(new BigDecimal("10.00")).metersPerSkein(100).color("azul")
                .build();
        MaterialStockCache.StockLevel stock = new MaterialStockCache.StockLevel(5, null);

        MaterialDto dto = mapper.toDto(def, stock);

        assertThat(dto.quantity()).isEqualTo(5);
        assertThat(dto.meters()).isEqualTo(100);
    }

    @Test
    void toDto_accessoryUsesStockQuantityOnly() {
        MaterialDefinition def = MaterialDefinition.builder()
                .id(UUID.randomUUID()).name("Botão").type(MaterialType.ACCESSORY)
                .price(BigDecimal.ONE).build();
        MaterialStockCache.StockLevel stock = new MaterialStockCache.StockLevel(20, null);

        MaterialDto dto = mapper.toDto(def, stock);

        assertThat(dto.quantity()).isEqualTo(20);
        assertThat(dto.meters()).isNull();
    }

    @Test
    void toDto_meterAccessoryUsesStockMetersOnly() {
        MaterialDefinition def = MaterialDefinition.builder()
                .id(UUID.randomUUID()).name("Fita").type(MaterialType.METER_ACCESSORY)
                .price(BigDecimal.ONE).build();
        MaterialStockCache.StockLevel stock = new MaterialStockCache.StockLevel(null, 30);

        MaterialDto dto = mapper.toDto(def, stock);

        assertThat(dto.meters()).isEqualTo(30);
        assertThat(dto.quantity()).isNull();
    }

    @Test
    void toDto_nullStockYieldsNullQuantityAndMeters() {
        MaterialDefinition def = MaterialDefinition.builder()
                .id(UUID.randomUUID()).name("Botão").type(MaterialType.ACCESSORY)
                .price(BigDecimal.ONE).build();

        MaterialDto dto = mapper.toDto(def, null);

        assertThat(dto.quantity()).isNull();
        assertThat(dto.meters()).isNull();
    }

    @Test
    void applyToDefinition_yarnKeepsColorAndMetersPerSkein() {
        MaterialDto in = new MaterialDto(null, "La Azul", MaterialType.YARN, new BigDecimal("12.5"), null, "azul", 5, 100);
        MaterialDefinition def = new MaterialDefinition();

        mapper.applyToDefinition(in, def);

        assertThat(def.getColor()).isEqualTo("azul");
        assertThat(def.getMetersPerSkein()).isEqualTo(100);
    }

    @Test
    void applyToDefinition_accessoryDiscardsColorAndMetersPerSkein() {
        MaterialDto in = new MaterialDto(null, "Botão", MaterialType.ACCESSORY, BigDecimal.ONE, null, "azul", 5, 100);
        MaterialDefinition def = new MaterialDefinition();

        mapper.applyToDefinition(in, def);

        assertThat(def.getColor()).isNull();
        assertThat(def.getMetersPerSkein()).isNull();
    }

    @Test
    void resolveStock_meterAccessoryUsesMetersField() {
        MaterialDto in = new MaterialDto(null, "Fita", MaterialType.METER_ACCESSORY, BigDecimal.ONE, null, null, null, 30);

        MaterialMapper.StockValues stock = mapper.resolveStock(in);

        assertThat(stock.meters()).isEqualTo(30);
        assertThat(stock.quantity()).isNull();
    }

    @Test
    void resolveStock_yarnUsesQuantityField() {
        MaterialDto in = new MaterialDto(null, "La Azul", MaterialType.YARN, BigDecimal.ONE, null, "azul", 5, 100);

        MaterialMapper.StockValues stock = mapper.resolveStock(in);

        assertThat(stock.quantity()).isEqualTo(5);
        assertThat(stock.meters()).isNull();
    }
}
