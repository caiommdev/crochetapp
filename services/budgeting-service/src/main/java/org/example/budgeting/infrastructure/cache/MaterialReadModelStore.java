package org.example.budgeting.infrastructure.cache;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MaterialReadModelStore {

    private static final String KEY_PREFIX = "material:";

    private final StringRedisTemplate redisTemplate;

    public void putStatic(
        UUID id, 
        String name, 
        MaterialType type, 
        BigDecimal price, 
        String color, 
        Integer metersPerSkein
    ) {
        Map<String, String> fields = Map.of(
                "name", nullToEmpty(name),
                "type", type.name(),
                "price", price == null ? "" : price.toPlainString(),
                "color", nullToEmpty(color),
                "metersPerSkein", metersPerSkein == null ? "" : metersPerSkein.toString()
        );
        redisTemplate.opsForHash().putAll(KEY_PREFIX + id, fields);
    }

    public void putStock(UUID id, Integer quantity, Integer meters) {
        Map<String, String> fields = Map.of(
                "stockQuantity", quantity == null ? "" : quantity.toString(),
                "stockMeters", meters == null ? "" : meters.toString()
        );
        redisTemplate.opsForHash().putAll(KEY_PREFIX + id, fields);
    }

    public void remove(UUID id) {
        redisTemplate.delete(KEY_PREFIX + id);
    }

    public Optional<MaterialView> findById(UUID id) {
        Map<Object, Object> fields = redisTemplate.opsForHash().entries(KEY_PREFIX + id);
        if (fields.isEmpty()) return Optional.empty();
        return Optional.of(toView(id, fields));
    }

    public List<MaterialView> findAllById(Collection<UUID> ids) {
        List<MaterialView> result = new ArrayList<>();
        for (UUID id : ids) {
            findById(id).ifPresent(result::add);
        }
        return result;
    }

    private MaterialView toView(UUID id, Map<Object, Object> fields) {
        MaterialType type = MaterialType.valueOf(toString(fields.get("type")));
        BigDecimal price = toDecimal(fields.get("price"));
        Integer stockQuantity = toInteger(fields.get("stockQuantity"));
        Integer stockMeters = toInteger(fields.get("stockMeters"));
        Integer metersPerSkein = toInteger(fields.get("metersPerSkein"));

        Integer quantity = null;
        Integer meters = null;
        switch (type) {
            case YARN -> {
                quantity = stockQuantity;
                meters = metersPerSkein;
            }
            case ACCESSORY -> quantity = stockQuantity;
            case METER_ACCESSORY -> meters = stockMeters;
        }

        return new MaterialView(
            id, 
            toString(fields.get("name")), 
            type, 
            price, 
            null, 
            toString(fields.get("color")), 
            quantity, 
            meters);
    }

    private String toString(Object value) {
        return value == null || value.toString().isBlank() ? null : value.toString();
    }

    private Integer toInteger(Object value) {
        String text = toString(value);
        return text == null ? null : Integer.parseInt(text);
    }

    private BigDecimal toDecimal(Object value) {
        String text = toString(value);
        return text == null ? null : new BigDecimal(text);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
