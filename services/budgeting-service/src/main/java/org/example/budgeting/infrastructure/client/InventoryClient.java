package org.example.budgeting.infrastructure.client;

import org.example.budgeting.application.dtos.ReservationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente REST para o Inventory Service. Diferente do acesso ao Catalog, aqui as falhas
 * (ex.: estoque insuficiente -> 409) DEVEM se propagar para abortar o aceite do orçamento.
 */
@Component
public class InventoryClient {

    private final RestClient client;

    public InventoryClient(RestClient.Builder builder,
                           @Value("${inventory.url:http://localhost:8082}") String inventoryUrl) {
        this.client = builder.baseUrl(inventoryUrl).build();
    }

    public void reserve(ReservationRequest request) {
        client.post().uri("/api/stock/reserve").body(request).retrieve().toBodilessEntity();
    }

    public void release(ReservationRequest request) {
        client.post().uri("/api/stock/release").body(request).retrieve().toBodilessEntity();
    }
}
