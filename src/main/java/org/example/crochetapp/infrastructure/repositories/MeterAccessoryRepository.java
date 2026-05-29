package org.example.crochetapp.infrastructure.repositories;

import java.util.UUID;

import org.example.crochetapp.domain.model.material.MeterAccessory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeterAccessoryRepository extends JpaRepository<MeterAccessory, UUID> {
}

