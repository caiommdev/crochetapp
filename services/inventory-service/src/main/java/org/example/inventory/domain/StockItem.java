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

/**
 * Nível de estoque de um material. A identidade ({@code materialId}) é definida pelo
 * Catalog Service — aqui é apenas uma referência lógica por ID, sem foreign key entre bancos.
 * {@code quantity} = unidades/novelos; {@code meters} = metros disponíveis. Ambos podem ser nulos
 * conforme o tipo do material (fio/acessório/acessório por metro).
 */
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
