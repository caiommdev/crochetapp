package org.example.budgeting.infrastructure.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.infrastructure.cache.PointRow;
import org.example.budgeting.infrastructure.cache.RecipeReadModelStore;
import org.example.budgeting.infrastructure.cache.RequirementRow;
import org.example.budgeting.infrastructure.messaging.events.RecipeDefined;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeDefinedListener {

    private final RecipeReadModelStore recipeReadModelStore;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.recipe-defined", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.catalog}", type = "topic", durable = "true"),
            key = "recipe.defined"
    ))
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
