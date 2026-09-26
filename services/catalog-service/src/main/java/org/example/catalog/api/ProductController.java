package org.example.catalog.api;

import java.util.List;
import java.util.UUID;

import org.example.catalog.api.dto.ProductDto;
import org.example.catalog.api.dto.SaveProductRequest;
import org.example.catalog.application.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> findAll() {
        log.info("HTTP list products recebido");
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findById(@PathVariable UUID id) {
        log.info("HTTP get product recebido productId={}", id);
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("HTTP get product retornando 404 productId={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody SaveProductRequest request) {
        log.info("HTTP create product recebido name={}", request.name());
        return ResponseEntity.ok(productService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable UUID id, @RequestBody SaveProductRequest request) {
        log.info("HTTP update product recebido productId={} name={}", id, request.name());
        return productService.update(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("HTTP update product retornando 404 productId={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("HTTP delete product recebido productId={}", id);
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
