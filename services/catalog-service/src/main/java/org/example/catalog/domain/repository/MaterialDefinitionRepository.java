package org.example.catalog.domain.repository;

import org.example.catalog.domain.model.MaterialDefinition;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaterialDefinitionRepository {

    List<MaterialDefinition> findAll();

    Optional<MaterialDefinition> findById(UUID id);

    List<MaterialDefinition> findAllById(Collection<UUID> ids);

    MaterialDefinition save(MaterialDefinition materialDefinition);

    void deleteById(UUID id);
}
