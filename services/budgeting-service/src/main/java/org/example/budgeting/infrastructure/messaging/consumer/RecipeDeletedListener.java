package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.infrastructure.cache.RecipeReadModelStore;
import org.example.budgeting.infrastructure.messaging.events.RecipeDeleted;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeDeletedListener {

    private final RecipeReadModelStore recipeReadModelStore;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.recipe-deleted", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.catalog}", type = "topic", durable = "true"),
            key = "recipe.deleted"
    ))
    public void handle(RecipeDeleted event) {
        recipeReadModelStore.remove(event.recipeId());
    }
}
