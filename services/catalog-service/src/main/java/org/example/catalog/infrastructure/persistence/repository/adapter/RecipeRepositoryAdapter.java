package org.example.catalog.infrastructure.persistence.repository.adapter;

import lombok.RequiredArgsConstructor;
import org.example.catalog.domain.model.Recipe;
import org.example.catalog.domain.repository.RecipeRepository;
import org.example.catalog.infrastructure.persistence.mappers.RecipeMapper;
import org.example.catalog.infrastructure.persistence.repository.jpa.RecipeJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RecipeRepositoryAdapter implements RecipeRepository {

    private final RecipeJpaRepository jpaRepository;
    private final RecipeMapper mapper;

    @Override
    public List<Recipe> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Recipe> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Recipe save(Recipe recipe) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(recipe)));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
