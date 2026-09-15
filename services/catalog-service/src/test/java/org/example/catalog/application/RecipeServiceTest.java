package org.example.catalog.application;

import org.example.catalog.api.dto.RecipeDto;
import org.example.catalog.api.dto.SaveRecipeRequest;
import org.example.catalog.domain.events.RecipeDefined;
import org.example.catalog.domain.events.RecipeDeleted;
import org.example.catalog.domain.model.Recipe;
import org.example.catalog.domain.repository.RecipeRepository;
import org.example.catalog.domain.shared.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private MaterialService materialService;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void save_publishesRecipeDefinedEvent() {
        SaveRecipeRequest request = new SaveRecipeRequest(
                "Amigurumi", "desc",
                List.of(new SaveRecipeRequest.PointDto("Ponto Alto", 5, 10)),
                List.of());
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> {
            Recipe recipe = invocation.getArgument(0);
            recipe.setId(UUID.randomUUID());
            return recipe;
        });
        when(materialService.findAsDtoMap(any())).thenReturn(Map.of());

        RecipeDto dto = recipeService.save(request);

        assertThat(dto.name()).isEqualTo("Amigurumi");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RecipeDefined>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventPublisher).publish(captor.capture());
        RecipeDefined event = captor.getValue().get(0);
        assertThat(event.name()).isEqualTo("Amigurumi");
        assertThat(event.points()).hasSize(1);
        assertThat(event.points().get(0).name()).isEqualTo("Ponto Alto");
    }

    @Test
    void deleteById_publishesRecipeDeletedEvent() {
        UUID id = UUID.randomUUID();

        recipeService.deleteById(id);

        verify(recipeRepository).deleteById(id);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RecipeDeleted>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().get(0).recipeId()).isEqualTo(id);
    }

    @Test
    void update_returnsEmptyWhenRecipeNotFound() {
        UUID id = UUID.randomUUID();
        when(recipeRepository.findById(id)).thenReturn(Optional.empty());
        SaveRecipeRequest request = new SaveRecipeRequest("x", "y", List.of(), List.of());

        assertThat(recipeService.update(id, request)).isEmpty();
    }
}
