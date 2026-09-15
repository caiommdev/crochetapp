package org.example.inventory.application;

import org.example.inventory.api.dto.ReservationRequest;
import org.example.inventory.api.dto.StockDto;
import org.example.inventory.domain.events.StockLevelChanged;
import org.example.inventory.domain.model.StockItem;
import org.example.inventory.domain.repository.StockItemRepository;
import org.example.inventory.domain.shared.DomainEventPublisher;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockItemRepository repository;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private StockService stockService;

    @Test
    void upsert_createsNewItemAndPublishesStockLevelChanged() {
        UUID materialId = UUID.randomUUID();
        StockDto dto = new StockDto(materialId, 10, null);
        when(repository.findById(materialId)).thenReturn(Optional.empty());
        when(repository.save(any(StockItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StockItem result = stockService.upsert(dto);

        assertThat(result.getQuantity()).isEqualTo(10);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<StockLevelChanged>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().get(0).materialId()).isEqualTo(materialId);
        assertThat(captor.getValue().get(0).quantity()).isEqualTo(10);
    }

    @Test
    void reserve_decrementsStockAndPublishesEventPerLine() {
        UUID materialId = UUID.randomUUID();
        StockItem item = StockItem.builder().materialId(materialId).quantity(10).meters(0).build();
        ReservationRequest request = new ReservationRequest(UUID.randomUUID(),
                List.of(new ReservationRequest.ReservationLine(materialId, 3, 0)));
        when(repository.findById(materialId)).thenReturn(Optional.of(item));
        when(repository.save(item)).thenReturn(item);

        stockService.reserve(request);

        assertThat(item.getQuantity()).isEqualTo(7);
        verify(eventPublisher, times(1)).publish(any());
    }

    @Test
    void reserve_throwsAndDoesNotSaveWhenMaterialNotFound() {
        UUID materialId = UUID.randomUUID();
        ReservationRequest request = new ReservationRequest(UUID.randomUUID(),
                List.of(new ReservationRequest.ReservationLine(materialId, 1, 0)));
        when(repository.findById(materialId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.reserve(request))
                .isInstanceOf(IllegalStateException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void reserve_throwsWhenStockInsufficient() {
        UUID materialId = UUID.randomUUID();
        StockItem item = StockItem.builder().materialId(materialId).quantity(1).meters(0).build();
        ReservationRequest request = new ReservationRequest(UUID.randomUUID(),
                List.of(new ReservationRequest.ReservationLine(materialId, 5, 0)));
        when(repository.findById(materialId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> stockService.reserve(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void release_addsBackQuantityAndPublishesEvent() {
        UUID materialId = UUID.randomUUID();
        StockItem item = StockItem.builder().materialId(materialId).quantity(2).meters(0).build();
        ReservationRequest request = new ReservationRequest(UUID.randomUUID(),
                List.of(new ReservationRequest.ReservationLine(materialId, 3, 0)));
        when(repository.findById(materialId)).thenReturn(Optional.of(item));
        when(repository.save(item)).thenReturn(item);

        stockService.release(request);

        assertThat(item.getQuantity()).isEqualTo(5);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<StockLevelChanged>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().get(0).quantity()).isEqualTo(5);
    }

    @Test
    void release_skipsWhenMaterialNotFound() {
        UUID materialId = UUID.randomUUID();
        ReservationRequest request = new ReservationRequest(UUID.randomUUID(),
                List.of(new ReservationRequest.ReservationLine(materialId, 3, 0)));
        when(repository.findById(materialId)).thenReturn(Optional.empty());

        stockService.release(request);

        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void deleteById_delegatesToRepository() {
        UUID materialId = UUID.randomUUID();

        stockService.deleteById(materialId);

        verify(repository).deleteById(materialId);
    }
}
