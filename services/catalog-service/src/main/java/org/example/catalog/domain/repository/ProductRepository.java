package org.example.catalog.domain.repository;

import org.example.catalog.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(UUID id);

    Product save(Product product);

    void deleteById(UUID id);
}
