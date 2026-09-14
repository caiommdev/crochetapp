package org.example.catalog.infrastructure.persistence.repository.jpa;

import org.example.catalog.infrastructure.persistence.entities.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecipeJpaRepository extends JpaRepository<RecipeEntity, UUID> {
}
