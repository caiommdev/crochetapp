package org.example.inventory.infrastructure.persistence.repository.jpa;

import org.example.inventory.infrastructure.persistence.entities.StockItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StockItemJpaRepository extends JpaRepository<StockItemEntity, UUID> {
}
