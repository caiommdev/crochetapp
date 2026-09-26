package org.example.catalog.application;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.example.catalog.api.dto.MaterialDto;
import org.example.catalog.domain.events.MaterialDefined;
import org.example.catalog.domain.events.MaterialDeleted;
import org.example.catalog.domain.model.MaterialDefinition;
import org.example.catalog.domain.repository.MaterialDefinitionRepository;
import org.example.catalog.domain.shared.DomainEventPublisher;
import org.example.catalog.infrastructure.cache.MaterialStockCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private static final Logger log = LoggerFactory.getLogger(MaterialService.class);

    private final MaterialDefinitionRepository repository;
    private final MaterialStockCache stockCache;
    private final DomainEventPublisher eventPublisher;
    private final MaterialMapper mapper;

    public List<MaterialDto> findAll() {
        List<MaterialDefinition> defs = repository.findAll();
        Map<UUID, MaterialStockCache.StockLevel> stock = stockCache.getAll(defs.stream().map(MaterialDefinition::getId).toList());
        return defs.stream().map(d -> mapper.toDto(d, stock.get(d.getId()))).toList();
    }

    public Optional<MaterialDto> findById(UUID id) {
        return repository.findById(id).map(d -> mapper.toDto(d, stockCache.get(id).orElse(null)));
    }

    public List<MaterialDto> findByIds(List<UUID> ids) {
        Map<UUID, MaterialDto> map = findAsDtoMap(ids);
        return ids.stream().map(map::get).filter(java.util.Objects::nonNull).toList();
    }

    public Map<UUID, MaterialDto> findAsDtoMap(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return Map.of();
        List<MaterialDefinition> defs = repository.findAllById(ids);
        Map<UUID, MaterialStockCache.StockLevel> stock = stockCache.getAll(ids);
        return defs.stream()
                .map(d -> mapper.toDto(d, stock.get(d.getId())))
                .collect(Collectors.toMap(MaterialDto::id, Function.identity(), (a, b) -> a));
    }

    public MaterialDto create(MaterialDto in) {
        log.info("Criando material name={} type={}", in.name(), in.type());
        MaterialDefinition def = new MaterialDefinition();
        mapper.applyToDefinition(in, def);
        def = repository.save(def);
        MaterialMapper.StockValues stock = mapper.resolveStock(in);
        stockCache.put(def.getId(), stock.quantity(), stock.meters());
        publishDefined(def, stock);
        log.info("Material criado materialId={} name={}", def.getId(), def.getName());
        return mapper.toDto(def, new MaterialStockCache.StockLevel(stock.quantity(), stock.meters()));
    }

    public Optional<MaterialDto> update(UUID id, MaterialDto in) {
        log.info("Atualizando material materialId={}", id);
        return repository.findById(id).map(def -> {
            mapper.applyToDefinition(in, def);
            def = repository.save(def);
            MaterialMapper.StockValues stock = mapper.resolveStock(in);
            stockCache.put(def.getId(), stock.quantity(), stock.meters());
            publishDefined(def, stock);
            log.info("Material atualizado materialId={} name={}", def.getId(), def.getName());
            return mapper.toDto(def, new MaterialStockCache.StockLevel(stock.quantity(), stock.meters()));
        });
    }

    public void deleteById(UUID id) {
        log.info("Removendo material materialId={}", id);
        repository.deleteById(id);
        stockCache.remove(id);
        eventPublisher.publish(List.of(new MaterialDeleted(id)));
        log.info("Material removido materialId={}", id);
    }

    private void publishDefined(MaterialDefinition def, MaterialMapper.StockValues stock) {
        eventPublisher.publish(List.of(new MaterialDefined(
                def.getId(), def.getName(), def.getType().name(), def.getPrice(),
                def.getColor(), def.getMetersPerSkein(), stock.quantity(), stock.meters())));
    }
}
