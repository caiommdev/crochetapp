package org.example.catalog.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MaterialStockCache {

    private static final String KEY_PREFIX = "stock:";

    private final StringRedisTemplate redisTemplate;

    public record StockLevel(Integer quantity, Integer meters) {}

    public void put(UUID materialId, Integer quantity, Integer meters) {
        Map<String, String> fields = new HashMap<>();
        fields.put("quantity", quantity == null ? "" : quantity.toString());
        fields.put("meters", meters == null ? "" : meters.toString());
        redisTemplate.opsForHash().putAll(KEY_PREFIX + materialId, fields);
    }

    public Optional<StockLevel> get(UUID materialId) {
        Map<Object, Object> fields = redisTemplate.opsForHash().entries(KEY_PREFIX + materialId);
        if (fields.isEmpty()) return Optional.empty();
        return Optional.of(new StockLevel(parseInt(fields.get("quantity")), parseInt(fields.get("meters"))));
    }

    public Map<UUID, StockLevel> getAll(Collection<UUID> materialIds) {
        Map<UUID, StockLevel> result = new HashMap<>();
        for (UUID materialId : materialIds) {
            get(materialId).ifPresent(level -> result.put(materialId, level));
        }
        return result;
    }

    public void remove(UUID materialId) {
        redisTemplate.delete(KEY_PREFIX + materialId);
    }

    private Integer parseInt(Object value) {
        if (value == null) return null;
        String text = value.toString();
        return text.isBlank() ? null : Integer.parseInt(text);
    }
}
