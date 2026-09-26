package org.example.budgeting.infrastructure.observability;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

public final class CorrelationContext {

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String TRACE_ID_KEY = "traceId";
    public static final String REQUEST_ID_KEY = "requestId";

    private CorrelationContext() {
    }

    public static MessagePostProcessor outboundHeaders() {
        return message -> {
            message.getMessageProperties().setHeader(TRACE_ID_HEADER, currentOrCreate(TRACE_ID_KEY));
            message.getMessageProperties().setHeader(REQUEST_ID_HEADER, currentOrCreate(REQUEST_ID_KEY));
            return message;
        };
    }

    public static void withMessage(Message message, Runnable action) {
        String traceId = readHeader(message, TRACE_ID_HEADER).orElseGet(() -> UUID.randomUUID().toString());
        String requestId = readHeader(message, REQUEST_ID_HEADER).orElseGet(() -> UUID.randomUUID().toString());
        MDC.put(TRACE_ID_KEY, traceId);
        MDC.put(REQUEST_ID_KEY, requestId);
        try {
            action.run();
        } finally {
            MDC.remove(TRACE_ID_KEY);
            MDC.remove(REQUEST_ID_KEY);
        }
    }

    private static Optional<String> readHeader(Message message, String name) {
        Object value = message.getMessageProperties().getHeaders().get(name);
        if (value instanceof byte[] bytes) {
            return Optional.of(new String(bytes, StandardCharsets.UTF_8)).filter(v -> !v.isBlank());
        }
        return Optional.ofNullable(value).map(String::valueOf).filter(v -> !v.isBlank());
    }

    private static String currentOrCreate(String key) {
        return Optional.ofNullable(MDC.get(key)).filter(value -> !value.isBlank())
                .orElseGet(() -> UUID.randomUUID().toString());
    }
}