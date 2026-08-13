package org.example.budgeting.infrastructure.client;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class CatalogClient {

    private final RestClient client;

    public CatalogClient(@LoadBalanced RestClient.Builder builder) {
        this.client = builder.baseUrl("http://catalog-service").build();
    }

    public ProductView getProduct(UUID productId) {
        ProductView product = client.get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .body(ProductView.class);
        if (product == null) {
            throw new IllegalArgumentException("Produto não encontrado: " + productId);
        }
        return product;
    }

    public List<MaterialView> getMaterials(Collection<UUID> materialIds) {
        if (materialIds == null || materialIds.isEmpty()) return List.of();
        MaterialView[] found = client.get()
                .uri(uriBuilder -> uriBuilder.path("/api/materials")
                        .queryParam("ids", materialIds.toArray())
                        .build())
                .retrieve()
                .body(MaterialView[].class);
        return found == null ? List.of() : List.of(found);
    }
}
