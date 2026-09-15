package org.example.inventory.infrastructure.persistence.repository.adapter;

import lombok.RequiredArgsConstructor;

import org.example.inventory.domain.model.StockItem;
import org.example.inventory.domain.repository.StockItemRepository;
import org.example.inventory.infrastructure.persistence.mappers.StockItemMapper;
import org.example.inventory.infrastructure.persistence.repository.jpa.StockItemJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StockItemRepositoryAdapter implements StockItemRepository {

    private final StockItemJpaRepository jpaRepository;
    private final StockItemMapper mapper;

    @Override
    public List<StockItem> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<StockItem> findById(UUID materialId) {
        return jpaRepository.findById(materialId).map(mapper::toDomain);
    }

    @Override
    public List<StockItem> findAllById(List<UUID> materialIds) {
        return jpaRepository.findAllById(materialIds).stream().map(mapper::toDomain).toList();
    }

    @Override
    public StockItem save(StockItem stockItem) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(stockItem)));
    }

    @Override
    public void deleteById(UUID materialId) {
        jpaRepository.deleteById(materialId);
    }
}
