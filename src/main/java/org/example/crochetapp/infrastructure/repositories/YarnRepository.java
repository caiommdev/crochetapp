 package org.example.crochetapp.infrastructure.repositories;

import org.example.crochetapp.domain.model.material.Yarn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface YarnRepository extends JpaRepository<Yarn, UUID> {

    List<Yarn> findByColorIgnoreCase(String color);
}

