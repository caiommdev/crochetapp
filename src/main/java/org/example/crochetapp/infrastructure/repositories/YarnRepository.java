 package org.example.crochetapp.infrastructure.repositories;

import org.example.crochetapp.domain.model.material.Yarn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YarnRepository extends JpaRepository<Yarn, Long> {

    List<Yarn> findByColorIgnoreCase(String color);
}

