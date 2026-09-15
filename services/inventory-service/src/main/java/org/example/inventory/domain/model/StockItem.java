package org.example.inventory.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import org.example.inventory.domain.shared.AggregateRoot;
import org.example.inventory.domain.events.StockLevelChanged;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockItem extends AggregateRoot {
    private UUID materialId;
    private Integer quantity;
    private Integer meters;


    public void reserveItem(int reserveQuantity, int reserveMeters) {
        int currentQuantity = this.quantity == null ? 0 : this.quantity;
        if (currentQuantity < reserveQuantity) {
                    throw new IllegalStateException("Estoque insuficiente (unidades) para o material "
                            + materialId + ". Necessário: " + reserveQuantity + ", disponível: " + currentQuantity);
        }

        int currentMeter = this.meters == null ? 0 : this.meters;
        if (currentMeter < reserveMeters) {
            throw new IllegalStateException("Estoque insuficiente (metros) para o material "
                    + materialId + ". Necessário: " + reserveMeters + "m, disponível: " + currentMeter + "m");
        }
        this.quantity -= reserveQuantity;
        this.meters -= reserveMeters;

        addEvent(new StockLevelChanged(materialId, this.quantity, this.meters));
    }
}
