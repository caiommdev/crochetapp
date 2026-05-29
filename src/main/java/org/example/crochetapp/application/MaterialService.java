package org.example.crochetapp.application;

import lombok.RequiredArgsConstructor;
import org.example.crochetapp.domain.model.material.Material;
import org.example.crochetapp.infrastructure.repositories.MaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    public List<Material> findAll() {
        return materialRepository.findAll();
    }

    public Optional<Material> findById(UUID id) {
        return materialRepository.findById(id);
    }

    public Material save(Material material) {
        return materialRepository.save(material);
    }

    public Optional<Material> update(UUID id, Material updated) {
        return materialRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setImage(updated.getImage());
            return materialRepository.save(existing);
        });
    }

    public void deleteById(UUID id) {
        materialRepository.deleteById(id);
    }
}
