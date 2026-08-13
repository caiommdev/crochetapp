package org.example.budgeting.infrastructure.client;
import org.example.budgeting.application.dtos.ReservationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient {

    private final RestClient client;

    public InventoryClient(@LoadBalanced RestClient.Builder builder) {
        this.client = builder.baseUrl("http://inventory-service").build();
    }

    public void reserve(ReservationRequest request) {
        client.post().uri("/api/stock/reserve").body(request).retrieve().toBodilessEntity();
    }

    public void release(ReservationRequest request) {
        client.post().uri("/api/stock/release").body(request).retrieve().toBodilessEntity();
    }
}
