package org.example.catalog.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cliente REST para o Inventory Service. O Catalog agrega o estoque nas respostas de material.
 *
 * Resiliência (Fase 1): se o Inventory estiver indisponível, o Catalog degrada com elegância —
 * retorna a definição do material sem os dados de estoque, em vez de quebrar a requisição.
 * Na Fase 5 este acoplamento síncrono será substituído por um read-model replicado via eventos.
 */
@Component
public class StockClient {

    private static final Logger log = LoggerFactory.getLogger(StockClient.class);

    private final RestClient client;

    public StockClient(RestClient.Builder builder,
                       @Value("${inventory.url:http://localhost:8082}") String inventoryUrl) {
        this.client = builder.baseUrl(inventoryUrl).build();
    }

    public Map<UUID, StockView> getByIds(Collection<UUID> materialIds) {
        if (materialIds == null || materialIds.isEmpty()) return Map.of();
        try {
            StockView[] found = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/stock")
                            .queryParam("ids", materialIds.toArray())
                            .build())
                    .retrieve()
                    .body(StockView[].class);
            if (found == null) return Map.of();
            return Arrays.stream(found)
                    .collect(Collectors.toMap(StockView::materialId, Function.identity(), (a, b) -> a));
        } catch (Exception e) {
            log.warn("Inventory indisponível ao buscar estoque; retornando sem estoque. Causa: {}", e.getMessage());
            return Map.of();
        }
    }

    public StockView getById(UUID materialId) {
        return getByIds(java.util.List.of(materialId)).get(materialId);
    }

    public void upsert(StockWriteDto dto) {
        try {
            client.post().uri("/api/stock").body(dto).retrieve().toBodilessEntity();
        } catch (Exception e) {
            log.warn("Falha ao gravar estoque no Inventory para material {}. Causa: {}", dto.materialId(), e.getMessage());
        }
    }

    public void delete(UUID materialId) {
        try {
            client.delete().uri("/api/stock/{id}", materialId).retrieve().toBodilessEntity();
        } catch (Exception e) {
            log.warn("Falha ao remover estoque no Inventory para material {}. Causa: {}", materialId, e.getMessage());
        }
    }
}
