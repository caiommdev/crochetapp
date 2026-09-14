package org.example.catalog.infrastructure.persistence.repository.jpa;

import org.example.catalog.infrastructure.persistence.entities.MaterialDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MaterialDefinitionJpaRepository extends JpaRepository<MaterialDefinitionEntity, UUID> {
}
