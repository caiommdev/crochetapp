package org.example.catalog.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.catalog.api.dto.ProductDto;
import org.example.catalog.api.dto.SaveProductRequest;
import org.example.catalog.domain.events.ProductDefined;
import org.example.catalog.domain.events.ProductDeleted;
import org.example.catalog.domain.model.Product;
import org.example.catalog.domain.model.Recipe;
import org.example.catalog.domain.repository.ProductRepository;
import org.example.catalog.domain.repository.RecipeRepository;
import org.example.catalog.domain.shared.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

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
        log.info("Criando produto name={}", request.name());
        Recipe recipe = resolveRecipe(request);
        Product product = Product.builder()
                .name(request.name())
                .recipe(recipe)
                .image(request.image())
                .build();
        product = productRepository.save(product);
        publishDefined(product);
        log.info("Produto criado productId={} recipeId={}", product.getId(), product.getRecipe().getId());
        return toDto(product);
    }

    @Transactional
    public Optional<ProductDto> update(UUID id, SaveProductRequest request) {
        log.info("Atualizando produto productId={}", id);
        return productRepository.findById(id).map(existing -> {
            existing.setName(request.name());
            existing.setRecipe(resolveRecipe(request));
            existing.setImage(request.image());
            Product saved = productRepository.save(existing);
            publishDefined(saved);
            log.info("Produto atualizado productId={} recipeId={}", saved.getId(), saved.getRecipe().getId());
            return toDto(saved);
        });
    }

    public void deleteById(UUID id) {
        log.info("Removendo produto productId={}", id);
        productRepository.deleteById(id);
        eventPublisher.publish(List.of(new ProductDeleted(id)));
        log.info("Produto removido productId={}", id);
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
            log.warn("Falha ao resolver receita do produto: recipe.id ausente");
            throw new IllegalArgumentException("Produto exige uma receita (recipe.id).");
        }
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    log.warn("Receita não encontrada para associação ao produto recipeId={}", recipeId);
                    return new IllegalArgumentException("Receita não encontrada: " + recipeId);
                });
    }
}
