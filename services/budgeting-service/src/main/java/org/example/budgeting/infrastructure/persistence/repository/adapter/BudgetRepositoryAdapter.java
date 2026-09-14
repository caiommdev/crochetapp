package org.example.budgeting.infrastructure.persistence.repository.adapter;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.domain.model.Budget;
import org.example.budgeting.domain.repository.BudgetRepository;
import org.example.budgeting.infrastructure.persistence.mappers.BudgetMapper;
import org.example.budgeting.infrastructure.persistence.repository.jpa.BudgetJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BudgetRepositoryAdapter implements BudgetRepository {

    private final BudgetJpaRepository jpaRepository;
    private final BudgetMapper mapper;

    @Override
    public List<Budget> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Budget> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Budget save(Budget budget) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(budget)));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
