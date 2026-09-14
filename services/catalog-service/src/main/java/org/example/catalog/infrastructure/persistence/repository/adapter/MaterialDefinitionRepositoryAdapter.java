package org.example.catalog.infrastructure.persistence.repository.adapter;

import lombok.RequiredArgsConstructor;
import org.example.catalog.domain.model.MaterialDefinition;
import org.example.catalog.domain.repository.MaterialDefinitionRepository;
import org.example.catalog.infrastructure.persistence.mappers.MaterialDefinitionMapper;
import org.example.catalog.infrastructure.persistence.repository.jpa.MaterialDefinitionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MaterialDefinitionRepositoryAdapter implements MaterialDefinitionRepository {

    private final MaterialDefinitionJpaRepository jpaRepository;
    private final MaterialDefinitionMapper mapper;

    @Override
    public List<MaterialDefinition> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<MaterialDefinition> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<MaterialDefinition> findAllById(Collection<UUID> ids) {
        return jpaRepository.findAllById(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public MaterialDefinition save(MaterialDefinition materialDefinition) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(materialDefinition)));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
