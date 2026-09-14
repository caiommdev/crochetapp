package org.example.catalog.infrastructure.persistence.repository.adapter;

import lombok.RequiredArgsConstructor;
import org.example.catalog.domain.model.Product;
import org.example.catalog.domain.repository.ProductRepository;
import org.example.catalog.infrastructure.persistence.mappers.ProductMapper;
import org.example.catalog.infrastructure.persistence.repository.jpa.ProductJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductMapper mapper;

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Product save(Product product) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(product)));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
