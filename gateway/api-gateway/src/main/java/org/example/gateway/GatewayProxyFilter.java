package org.example.gateway;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Gateway leve: porta única de entrada que roteia /api/** para o microsserviço dono do recurso,
 * com base no prefixo do caminho, e aplica CORS de forma centralizada.
 *
 * Nota de evolução: numa próxima fase, este proxy artesanal pode ser trocado por Spring Cloud
 * Gateway, ganhando rate limiting, service discovery e filtros declarativos.
 */
@Component
public class GatewayProxyFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayProxyFilter.class);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final String catalogUrl;
    private final String inventoryUrl;
    private final String budgetingUrl;
    private final String allowedOrigins;

    public GatewayProxyFilter(
            @Value("${catalog.url:http://localhost:8081}") String catalogUrl,
            @Value("${inventory.url:http://localhost:8082}") String inventoryUrl,
            @Value("${budgeting.url:http://localhost:8083}") String budgetingUrl,
            @Value("${cors.allowed-origins:http://localhost:3000}") String allowedOrigins) {
        this.catalogUrl = catalogUrl;
        this.inventoryUrl = inventoryUrl;
        this.budgetingUrl = budgetingUrl;
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        applyCors(response);

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = request.getRequestURI();
        String base = resolveTarget(path);
        if (base == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Rota não mapeada no gateway: " + path);
            return;
        }

        String query = request.getQueryString();
        URI target = URI.create(base + path + (query != null ? "?" + query : ""));

        try {
            byte[] body = request.getInputStream().readAllBytes();
            HttpRequest.BodyPublisher publisher = body.length > 0
                    ? HttpRequest.BodyPublishers.ofByteArray(body)
                    : HttpRequest.BodyPublishers.noBody();

            HttpRequest.Builder builder = HttpRequest.newBuilder(target)
                    .timeout(Duration.ofSeconds(30))
                    .method(request.getMethod(), publisher);

            String contentType = request.getContentType();
            if (contentType != null) builder.header("Content-Type", contentType);
            String auth = request.getHeader("Authorization");
            if (auth != null) builder.header("Authorization", auth);

            HttpResponse<byte[]> upstream = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());

            response.setStatus(upstream.statusCode());
            upstream.headers().firstValue("Content-Type").ifPresent(response::setContentType);
            response.getOutputStream().write(upstream.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            response.sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT, "Serviço interrompido.");
        } catch (Exception e) {
            log.warn("Falha ao encaminhar {} {} -> {}: {}", request.getMethod(), path, target, e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Serviço indisponível: " + e.getMessage());
        }
    }

    private String resolveTarget(String path) {
        if (path.startsWith("/api/materials") || path.startsWith("/api/recipes") || path.startsWith("/api/products")) {
            return catalogUrl;
        }
        if (path.startsWith("/api/stock")) {
            return inventoryUrl;
        }
        if (path.startsWith("/api/budgets")) {
            return budgetingUrl;
        }
        return null;
    }

    private void applyCors(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", allowedOrigins);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}
