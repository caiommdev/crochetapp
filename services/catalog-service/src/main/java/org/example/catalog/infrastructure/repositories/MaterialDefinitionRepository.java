package org.example.catalog.infrastructure.repositories;

import org.example.catalog.domain.model.MaterialDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MaterialDefinitionRepository extends JpaRepository<MaterialDefinition, UUID> {
}
