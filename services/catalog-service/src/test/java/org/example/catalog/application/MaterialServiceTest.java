package org.example.catalog.application;

import org.example.catalog.api.dto.MaterialDto;
import org.example.catalog.domain.enums.MaterialType;
import org.example.catalog.domain.events.MaterialDefined;
import org.example.catalog.domain.events.MaterialDeleted;
import org.example.catalog.domain.model.MaterialDefinition;
import org.example.catalog.domain.repository.MaterialDefinitionRepository;
import org.example.catalog.domain.shared.DomainEventPublisher;
import org.example.catalog.infrastructure.cache.MaterialStockCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private MaterialDefinitionRepository repository;
    @Mock
    private MaterialStockCache stockCache;
    @Mock
    private DomainEventPublisher eventPublisher;

    private MaterialService materialService;

    @BeforeEach
    void setUp() {
        materialService = new MaterialService(repository, stockCache, eventPublisher, new MaterialMapper());
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThat(materialService.findById(id)).isEmpty();
    }

    @Test
    void create_savesCachesStockAndPublishesMaterialDefined() {
        MaterialDto in = new MaterialDto(null, "La Azul", MaterialType.YARN, new BigDecimal("10.00"), null, "azul", 5, 100);
        when(repository.save(any(MaterialDefinition.class))).thenAnswer(invocation -> {
            MaterialDefinition def = invocation.getArgument(0);
            def.setId(UUID.randomUUID());
            return def;
        });

        MaterialDto result = materialService.create(in);

        assertThat(result.quantity()).isEqualTo(5);
        assertThat(result.meters()).isEqualTo(100);

        verify(stockCache).put(any(UUID.class), eq(5), isNull());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<MaterialDefined>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventPublisher).publish(captor.capture());
        MaterialDefined event = captor.getValue().get(0);
        assertThat(event.name()).isEqualTo("La Azul");
        assertThat(event.type()).isEqualTo("YARN");
        assertThat(event.quantity()).isEqualTo(5);
    }

    @Test
    void deleteById_evictsCacheAndPublishesMaterialDeleted() {
        UUID id = UUID.randomUUID();

        materialService.deleteById(id);

        verify(repository).deleteById(id);
        verify(stockCache).remove(id);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<MaterialDeleted>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).materialId()).isEqualTo(id);
    }
}
