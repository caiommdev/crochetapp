package org.example.catalog.infrastructure.messaging.rabbit;

import java.util.Map;

import org.example.catalog.domain.events.MaterialDefined;
import org.example.catalog.domain.events.MaterialDeleted;
import org.example.catalog.domain.events.ProductDefined;
import org.example.catalog.domain.events.ProductDeleted;
import org.example.catalog.domain.events.RecipeDefined;
import org.example.catalog.domain.events.RecipeDeleted;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitEventRouting {

    private final String exchange;
    private final Map<Class<?>, String> routingKeysByEventType;

    public RabbitEventRouting(@Value("${rabbitmq.exchange.catalog}") String exchange) {
        this.exchange = exchange;
        this.routingKeysByEventType = Map.of(
                MaterialDefined.class, "material.defined",
                MaterialDeleted.class, "material.deleted",
                RecipeDefined.class, "recipe.defined",
                RecipeDeleted.class, "recipe.deleted",
                ProductDefined.class, "product.defined",
                ProductDeleted.class, "product.deleted"
        );
    }

    public String exchange() {
        return exchange;
    }

    public String routingKey(Class<?> eventType) {
        String routingKey = routingKeysByEventType.get(eventType);
        if (routingKey == null) {
            throw new IllegalArgumentException("Nenhuma routing key configurada para o evento: " + eventType.getName());
        }
        return routingKey;
    }
}
