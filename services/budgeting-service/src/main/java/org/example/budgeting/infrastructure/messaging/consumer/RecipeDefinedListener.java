package org.example.budgeting.infrastructure.messaging.consumer;

import org.example.budgeting.infrastructure.cache.PointRow;
import org.example.budgeting.infrastructure.cache.RecipeReadModelStore;
import org.example.budgeting.infrastructure.cache.RequirementRow;
import org.example.budgeting.infrastructure.messaging.events.RecipeDefined;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecipeDefinedListener {

    private final RecipeReadModelStore recipeReadModelStore;

    @KafkaListener(
            topics = "${kafka.topic.recipe-defined}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "recipeDefinedContainerFactory"
    )
    public void handle(RecipeDefined event) {
        var points = event.points().stream()
                .map(p -> new PointRow(p.name(), p.centimetersPerPoint(), p.quantity()))
                .toList();
        var requirements = event.materialRequirements().stream()
                .map(r -> new RequirementRow(r.materialId(), r.quantityNeeded()))
                .toList();
        recipeReadModelStore.putDefined(event.recipeId(), event.name(), event.description(), points, requirements);
    }
}
