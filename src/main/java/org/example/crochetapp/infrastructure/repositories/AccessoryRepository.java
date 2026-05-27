package org.example.crochetapp.infrastructure.persistence;

import org.example.crochetapp.domain.model.material.Accessory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessoryRepository extends JpaRepository<Accessory, Long> {
}

