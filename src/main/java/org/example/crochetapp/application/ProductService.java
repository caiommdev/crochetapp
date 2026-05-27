package org.example.crochetapp.application;

import lombok.RequiredArgsConstructor;
import org.example.crochetapp.domain.model.Product;
import org.example.crochetapp.infrastructure.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> update(UUID id, Product updated) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setRecipe(updated.getRecipe());
            existing.setImage(updated.getImage());
            return productRepository.save(existing);
        });
    }

    public void deleteById(UUID id) {
        productRepository.deleteById(id);
    }
}
