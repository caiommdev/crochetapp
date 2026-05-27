package org.example.crochetapp.application;

import lombok.RequiredArgsConstructor;
import org.example.crochetapp.domain.model.Recipe;
import org.example.crochetapp.infrastructure.repositories.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> findById(UUID id) {
        return recipeRepository.findById(id);
    }

    public Recipe save(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public Optional<Recipe> update(UUID id, Recipe updated) {
        return recipeRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setDescription(updated.getDescription());
            existing.setPoints(updated.getPoints());
            existing.setImage(updated.getImage());
            return recipeRepository.save(existing);
        });
    }

    public void deleteById(UUID id) {
        recipeRepository.deleteById(id);
    }
}
