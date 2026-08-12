package org.example.catalog.application;

import lombok.RequiredArgsConstructor;
import org.example.catalog.api.dto.MaterialDto;
import org.example.catalog.domain.model.MaterialDefinition;
import org.example.catalog.infrastructure.client.StockClient;
import org.example.catalog.infrastructure.client.StockView;
import org.example.catalog.infrastructure.client.StockWriteDto;
import org.example.catalog.infrastructure.repositories.MaterialDefinitionRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialDefinitionRepository repository;
    private final StockClient stockClient;
    private final MaterialMapper mapper;

    public List<MaterialDto> findAll() {
        List<MaterialDefinition> defs = repository.findAll();
        Map<UUID, StockView> stock = stockClient.getByIds(defs.stream().map(MaterialDefinition::getId).toList());
        return defs.stream().map(d -> mapper.toDto(d, stock.get(d.getId()))).toList();
    }

    public Optional<MaterialDto> findById(UUID id) {
        return repository.findById(id).map(d -> mapper.toDto(d, stockClient.getById(id)));
    }

    /** Retorna materiais por ids, preservando a ordem solicitada. */
    public List<MaterialDto> findByIds(List<UUID> ids) {
        Map<UUID, MaterialDto> map = findAsDtoMap(ids);
        return ids.stream().map(map::get).filter(java.util.Objects::nonNull).toList();
    }

    /** Resolve uma coleção de ids em MaterialDto (definição + estoque). Usado por receitas/produtos. */
    public Map<UUID, MaterialDto> findAsDtoMap(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return Map.of();
        List<MaterialDefinition> defs = repository.findAllById(ids);
        Map<UUID, StockView> stock = stockClient.getByIds(ids);
        return defs.stream()
                .map(d -> mapper.toDto(d, stock.get(d.getId())))
                .collect(Collectors.toMap(MaterialDto::id, Function.identity(), (a, b) -> a));
    }

    public MaterialDto create(MaterialDto in) {
        MaterialDefinition def = new MaterialDefinition();
        mapper.applyToDefinition(in, def);
        def = repository.save(def);
        StockWriteDto stock = mapper.toStockWrite(def.getId(), in);
        stockClient.upsert(stock);
        return mapper.toDto(def, new StockView(def.getId(), stock.quantity(), stock.meters()));
    }

    public Optional<MaterialDto> update(UUID id, MaterialDto in) {
        return repository.findById(id).map(def -> {
            mapper.applyToDefinition(in, def);
            def = repository.save(def);
            StockWriteDto stock = mapper.toStockWrite(def.getId(), in);
            stockClient.upsert(stock);
            return mapper.toDto(def, new StockView(def.getId(), stock.quantity(), stock.meters()));
        });
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
        stockClient.delete(id);
    }
}
