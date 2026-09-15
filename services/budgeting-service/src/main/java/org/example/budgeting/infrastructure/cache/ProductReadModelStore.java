package org.example.budgeting.infrastructure.cache;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductReadModelStore {

    private static final String KEY_PREFIX = "product:";

    private final StringRedisTemplate redisTemplate;
    private final RecipeReadModelStore recipeReadModelStore;

    public void putDefined(UUID id, String name, UUID recipeId) {
        Map<String, String> fields = Map.of(
                "name", name == null ? "" : name,
                "recipeId", recipeId.toString()
        );
        redisTemplate.opsForHash().putAll(KEY_PREFIX + id, fields);
    }

    public void remove(UUID id) {
        redisTemplate.delete(KEY_PREFIX + id);
    }

    public Optional<ProductView> findById(UUID id) {
        Map<Object, Object> fields = redisTemplate.opsForHash().entries(KEY_PREFIX + id);
        if (fields.isEmpty()) return Optional.empty();

        UUID recipeId = UUID.fromString(fields.get("recipeId").toString());
        RecipeView recipe = recipeReadModelStore.findById(recipeId).orElse(null);
        String name = fields.get("name") == null ? null : fields.get("name").toString();

        return Optional.of(new ProductView(id, name, recipe, null));
    }
}
