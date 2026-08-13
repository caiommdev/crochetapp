package org.example.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stock_items")
public class StockItem {

    @Id
    @Column(name = "material_id", updatable = false, nullable = false)
    private UUID materialId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "meters")
    private Integer meters;
}
