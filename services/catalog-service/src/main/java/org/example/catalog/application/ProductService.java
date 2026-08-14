package org.example.catalog.application;

import lombok.RequiredArgsConstructor;
import org.example.catalog.api.dto.ProductDto;
import org.example.catalog.api.dto.SaveProductRequest;
import org.example.catalog.domain.model.Product;
import org.example.catalog.domain.model.Recipe;
import org.example.catalog.infrastructure.repositories.ProductRepository;
import org.example.catalog.infrastructure.repositories.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeService recipeService;

    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return productRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<ProductDto> findById(UUID id) {
        return productRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public ProductDto create(SaveProductRequest request) {
        Recipe recipe = resolveRecipe(request);
        Product product = Product.builder()
                .name(request.name())
                .recipe(recipe)
                .image(request.image())
                .build();
        return toDto(productRepository.save(product));
    }

    @Transactional
    public Optional<ProductDto> update(UUID id, SaveProductRequest request) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(request.name());
            existing.setRecipe(resolveRecipe(request));
            existing.setImage(request.image());
            return toDto(productRepository.save(existing));
        });
    }

    public void deleteById(UUID id) {
        productRepository.deleteById(id);
    }

    public ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                recipeService.toDto(product.getRecipe()),
                product.getImage());
    }

    private Recipe resolveRecipe(SaveProductRequest request) {
        UUID recipeId = request.recipe() != null ? request.recipe().id() : null;
        if (recipeId == null) {
            throw new IllegalArgumentException("Produto exige uma receita (recipe.id).");
        }
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Receita não encontrada: " + recipeId));
    }
}
