package org.example.budgeting.infrastructure.cache;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecipeReadModelStore {

    private static final String KEY_PREFIX = "recipe:";

    private final StringRedisTemplate redisTemplate;
    private final MaterialReadModelStore materialReadModelStore;

    public void putDefined(UUID id, String name, String description,
                            List<PointRow> points, List<RequirementRow> requirements) {
        Map<String, String> fields = Map.of(
                "name", nullToEmpty(name),
                "description", nullToEmpty(description),
                "points", encodePoints(points),
                "requirements", encodeRequirements(requirements)
        );
        redisTemplate.opsForHash().putAll(KEY_PREFIX + id, fields);
    }

    public void remove(UUID id) {
        redisTemplate.delete(KEY_PREFIX + id);
    }

    public Optional<RecipeView> findById(UUID id) {
        Map<Object, Object> fields = redisTemplate.opsForHash().entries(KEY_PREFIX + id);
        if (fields.isEmpty()) return Optional.empty();

        List<PointView> points = decodePoints(toString(fields.get("points")));
        List<RequirementView> requirements = decodeRequirements(toString(fields.get("requirements"))).stream()
                .map(row -> new RequirementView(materialReadModelStore.findById(row.materialId()).orElse(null), row.quantityNeeded()))
                .toList();

        return Optional.of(new RecipeView(id, toString(fields.get("name")), toString(fields.get("description")), points, null, requirements));
    }

    private String encodePoints(List<PointRow> points) {
        if (points == null || points.isEmpty()) return "";
        return points.stream()
                .map(p -> String.join(",", encode(p.name()), encode(toString(p.centimetersPerPoint())), encode(toString(p.quantity()))))
                .collect(Collectors.joining(";"));
    }

    private List<PointView> decodePoints(String encoded) {
        if (encoded == null || encoded.isBlank()) return List.of();
        return Arrays.stream(encoded.split(";"))
                .map(row -> row.split(",", -1))
                .map(parts -> new PointView(decode(parts[0]), toInteger(decode(parts[1])), toInteger(decode(parts[2]))))
                .toList();
    }

    private String encodeRequirements(List<RequirementRow> requirements) {
        if (requirements == null || requirements.isEmpty()) return "";
        return requirements.stream()
                .map(r -> String.join(",", encode(r.materialId().toString()), encode(toString(r.quantityNeeded()))))
                .collect(Collectors.joining(";"));
    }

    private List<RequirementRow> decodeRequirements(String encoded) {
        if (encoded == null || encoded.isBlank()) return List.of();
        return Arrays.stream(encoded.split(";"))
                .map(row -> row.split(",", -1))
                .map(parts -> new RequirementRow(UUID.fromString(decode(parts[0])), toInteger(decode(parts[1]))))
                .toList();
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private Integer toInteger(String value) {
        return value == null || value.isBlank() ? null : Integer.parseInt(value);
    }

    private String toString(Object value) {
        return value == null ? null : value.toString();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
