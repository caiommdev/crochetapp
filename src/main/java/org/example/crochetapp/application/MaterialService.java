package org.example.crochetapp.application;

import lombok.RequiredArgsConstructor;
import org.example.crochetapp.domain.model.material.Material;
import org.example.crochetapp.infrastructure.repositories.MaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    public List<Material> findAll() {
        return materialRepository.findAll();
    }

    public Optional<Material> findById(Long id) {
        return materialRepository.findById(id);
    }

    public Material save(Material material) {
        return materialRepository.save(material);
    }

    public Optional<Material> update(Long id, Material updated) {
        return materialRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setImage(updated.getImage());
            return materialRepository.save(existing);
        });
    }

    public void deleteById(Long id) {
        materialRepository.deleteById(id);
    }
}
