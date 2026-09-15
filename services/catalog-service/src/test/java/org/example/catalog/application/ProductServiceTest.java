package org.example.catalog.application;

import org.example.catalog.api.dto.ProductDto;
import org.example.catalog.api.dto.RecipeDto;
import org.example.catalog.api.dto.SaveProductRequest;
import org.example.catalog.domain.events.ProductDefined;
import org.example.catalog.domain.events.ProductDeleted;
import org.example.catalog.domain.model.Product;
import org.example.catalog.domain.model.Recipe;
import org.example.catalog.domain.repository.ProductRepository;
import org.example.catalog.domain.repository.RecipeRepository;
import org.example.catalog.domain.shared.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private RecipeService recipeService;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private ProductService productService;

    @Test
    void create_throwsWhenRecipeRefIsMissing() {
        SaveProductRequest request = new SaveProductRequest("Boneca", null, null);

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_throwsWhenRecipeNotFound() {
        UUID recipeId = UUID.randomUUID();
        SaveProductRequest request = new SaveProductRequest("Boneca", new SaveProductRequest.RecipeRef(recipeId), null);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_savesAndPublishesProductDefinedEvent() {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = Recipe.builder().id(recipeId).name("Receita").build();
        SaveProductRequest request = new SaveProductRequest("Boneca", new SaveProductRequest.RecipeRef(recipeId), null);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(UUID.randomUUID());
            return product;
        });
        when(recipeService.toDto(recipe)).thenReturn(
                new RecipeDto(recipeId, "Receita", null, List.of(), null, List.of()));

        ProductDto dto = productService.create(request);

        assertThat(dto.name()).isEqualTo("Boneca");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ProductDefined>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventPublisher).publish(captor.capture());
        ProductDefined event = captor.getValue().get(0);
        assertThat(event.name()).isEqualTo("Boneca");
        assertThat(event.recipeId()).isEqualTo(recipeId);
    }

    @Test
    void deleteById_publishesProductDeletedEvent() {
        UUID id = UUID.randomUUID();

        productService.deleteById(id);

        verify(productRepository).deleteById(id);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ProductDeleted>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().get(0).productId()).isEqualTo(id);
    }
}
