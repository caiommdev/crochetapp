package org.example.inventory.infrastructure;

import org.example.inventory.domain.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StockItemRepository extends JpaRepository<StockItem, UUID> {
}
