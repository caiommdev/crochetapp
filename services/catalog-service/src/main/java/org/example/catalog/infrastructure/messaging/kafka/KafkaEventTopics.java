package org.example.catalog.infrastructure.messaging.kafka;

import org.example.catalog.domain.events.MaterialDefined;
import org.example.catalog.domain.events.MaterialDeleted;
import org.example.catalog.domain.events.ProductDefined;
import org.example.catalog.domain.events.ProductDeleted;
import org.example.catalog.domain.events.RecipeDefined;
import org.example.catalog.domain.events.RecipeDeleted;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaEventTopics {

    private final Map<Class<?>, String> topicsByEventType;

    public KafkaEventTopics(
            @Value("${kafka.topic.material-defined}") String materialDefined,
            @Value("${kafka.topic.material-deleted}") String materialDeleted,
            @Value("${kafka.topic.recipe-defined}") String recipeDefined,
            @Value("${kafka.topic.recipe-deleted}") String recipeDeleted,
            @Value("${kafka.topic.product-defined}") String productDefined,
            @Value("${kafka.topic.product-deleted}") String productDeleted
    ) {
        this.topicsByEventType = Map.of(
                MaterialDefined.class, materialDefined,
                MaterialDeleted.class, materialDeleted,
                RecipeDefined.class, recipeDefined,
                RecipeDeleted.class, recipeDeleted,
                ProductDefined.class, productDefined,
                ProductDeleted.class, productDeleted
        );
    }

    public String resolve(Class<?> eventType) {
        String topic = topicsByEventType.get(eventType);
        if (topic == null) {
            throw new IllegalArgumentException("Nenhum tópico Kafka configurado para o evento: " + eventType.getName());
        }
        return topic;
    }
}
