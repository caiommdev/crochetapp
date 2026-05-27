package org.example.crochetapp.infrastructure.repositories;

import org.example.crochetapp.domain.model.material.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório polimórfico sobre a hierarquia de Material (SINGLE_TABLE inheritance).
 * Permite consultas que retornem qualquer subtipo (Yarn, Accessory, MeterAccessory).
 */
@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
}

