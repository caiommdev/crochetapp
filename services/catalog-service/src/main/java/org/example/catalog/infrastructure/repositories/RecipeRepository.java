package org.example.catalog.infrastructure.repositories;

import org.example.catalog.domain.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
}
