package org.example.inventory.domain.repository;

import org.example.inventory.domain.StockItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockItemRepository {

    List<StockItem> findAll();

    Optional<StockItem> findById(UUID materialId);

    List<StockItem> findAllById(List<UUID> materialIds);

    StockItem save(StockItem stockItem);

    void deleteById(UUID materialId);
}
