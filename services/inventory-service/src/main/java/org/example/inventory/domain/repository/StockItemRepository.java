package org.example.inventory.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.inventory.domain.model.StockItem;

public interface StockItemRepository {

    List<StockItem> findAll();

    Optional<StockItem> findById(UUID materialId);

    List<StockItem> findAllById(List<UUID> materialIds);

    StockItem save(StockItem stockItem);

    void deleteById(UUID materialId);
}
