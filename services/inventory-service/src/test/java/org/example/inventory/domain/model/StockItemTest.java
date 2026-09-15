package org.example.inventory.domain.model;

import org.example.inventory.domain.events.StockLevelChanged;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockItemTest {

    @Test
    void reserveItem_decrementsQuantityAndMetersAndRaisesEvent() {
        UUID materialId = UUID.randomUUID();
        StockItem item = StockItem.builder().materialId(materialId).quantity(10).meters(50).build();

        item.reserveItem(4, 20);

        assertThat(item.getQuantity()).isEqualTo(6);
        assertThat(item.getMeters()).isEqualTo(30);
        assertThat(item.getEvents()).hasSize(1);
        StockLevelChanged event = (StockLevelChanged) item.getEvents().get(0);
        assertThat(event.materialId()).isEqualTo(materialId);
        assertThat(event.quantity()).isEqualTo(6);
        assertThat(event.meters()).isEqualTo(30);
    }

    @Test
    void reserveItem_throwsWhenQuantityInsufficient() {
        StockItem item = StockItem.builder().materialId(UUID.randomUUID()).quantity(2).meters(50).build();

        assertThatThrownBy(() -> item.reserveItem(5, 0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("unidades");

        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    void reserveItem_throwsWhenMetersInsufficient() {
        StockItem item = StockItem.builder().materialId(UUID.randomUUID()).quantity(10).meters(5).build();

        assertThatThrownBy(() -> item.reserveItem(1, 10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("metros");
    }

    @Test
    void reserveItem_treatsNullQuantityAndMetersAsZero() {
        StockItem item = StockItem.builder().materialId(UUID.randomUUID()).build();

        assertThatThrownBy(() -> item.reserveItem(1, 0))
                .isInstanceOf(IllegalStateException.class);
    }
}
