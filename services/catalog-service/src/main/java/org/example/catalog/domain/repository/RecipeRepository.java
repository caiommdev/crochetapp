package org.example.catalog.domain.repository;

import org.example.catalog.domain.model.Recipe;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository {

    List<Recipe> findAll();

    Optional<Recipe> findById(UUID id);

    Recipe save(Recipe recipe);

    void deleteById(UUID id);
}
