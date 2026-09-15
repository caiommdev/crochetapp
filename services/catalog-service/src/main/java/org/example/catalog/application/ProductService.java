package org.example.catalog.application;

import lombok.RequiredArgsConstructor;
import org.example.catalog.api.dto.ProductDto;
import org.example.catalog.api.dto.SaveProductRequest;
import org.example.catalog.domain.events.ProductDefined;
import org.example.catalog.domain.events.ProductDeleted;
import org.example.catalog.domain.model.Product;
import org.example.catalog.domain.model.Recipe;
import org.example.catalog.domain.repository.ProductRepository;
import org.example.catalog.domain.repository.RecipeRepository;
import org.example.catalog.domain.shared.DomainEventPublisher;
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
    private final DomainEventPublisher eventPublisher;

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
        product = productRepository.save(product);
        publishDefined(product);
        return toDto(product);
    }

    @Transactional
    public Optional<ProductDto> update(UUID id, SaveProductRequest request) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(request.name());
            existing.setRecipe(resolveRecipe(request));
            existing.setImage(request.image());
            Product saved = productRepository.save(existing);
            publishDefined(saved);
            return toDto(saved);
        });
    }

    public void deleteById(UUID id) {
        productRepository.deleteById(id);
        eventPublisher.publish(List.of(new ProductDeleted(id)));
    }

    private void publishDefined(Product product) {
        eventPublisher.publish(List.of(new ProductDefined(
                product.getId(), product.getName(), product.getRecipe().getId())));
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
