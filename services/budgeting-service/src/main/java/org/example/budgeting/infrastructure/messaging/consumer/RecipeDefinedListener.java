package org.example.budgeting.infrastructure.messaging.consumer;

import org.example.budgeting.infrastructure.cache.PointRow;
import org.example.budgeting.infrastructure.cache.RecipeReadModelStore;
import org.example.budgeting.infrastructure.cache.RequirementRow;
import org.example.budgeting.infrastructure.messaging.events.RecipeDefined;
import org.example.budgeting.infrastructure.observability.CorrelationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecipeDefinedListener {

        private static final Logger log = LoggerFactory.getLogger(RecipeDefinedListener.class);

    private final RecipeReadModelStore recipeReadModelStore;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "budgeting.recipe-defined", durable = "true"),
            exchange = @Exchange(name = "${rabbitmq.exchange.catalog}", type = "topic", durable = "true"),
            key = "recipe.defined"
    ))
    public void handle(RecipeDefined event, Message message) {
        CorrelationContext.withMessage(message, () -> {
            log.info("Evento recipe.defined recebido recipeId={} pointCount={} requirementCount={}",
                    event.recipeId(), event.points().size(), event.materialRequirements().size());
            var points = event.points().stream()
                    .map(p -> new PointRow(p.name(), p.centimetersPerPoint(), p.quantity()))
                    .toList();
            var requirements = event.materialRequirements().stream()
                    .map(r -> new RequirementRow(r.materialId(), r.quantityNeeded()))
                    .toList();
            recipeReadModelStore.putDefined(event.recipeId(), event.name(), event.description(), points, requirements);
            log.info("Read model de receita atualizado recipeId={}", event.recipeId());
        });
    }
}
