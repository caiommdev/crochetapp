package org.example.budgeting.infrastructure.messaging.consumer;

import org.example.budgeting.infrastructure.cache.RecipeReadModelStore;
import org.example.budgeting.infrastructure.messaging.events.RecipeDeleted;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecipeDeletedListener {

    private final RecipeReadModelStore recipeReadModelStore;

    @KafkaListener(
            topics = "${kafka.topic.recipe-deleted}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "recipeDeletedContainerFactory"
    )
    public void handle(RecipeDeleted event) {
        recipeReadModelStore.remove(event.recipeId());
    }
}
