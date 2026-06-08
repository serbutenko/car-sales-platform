package ru.butenko.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.butenko.application.abstractions.CustomOrderRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.orders.custom.CustomOrder;
import ru.butenko.persistence.mapper.CustomOrderEntityMapper;
import ru.butenko.persistence.repository.SpringDataCustomOrderRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class JpaCustomOrderRepositoryAdapter implements CustomOrderRepository {

    private final SpringDataCustomOrderRepository repository;
    private final CustomOrderEntityMapper mapper;

    @Override
    public CustomOrder save(CustomOrder entity) {
        return mapper.toDomain(repository.save(mapper.toEntity(entity)));
    }

    @Override
    public CustomOrder findById(UUID id) {
        return repository.findByIdAndRemovedFalse(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new DomainValidationException("Entity not found: " + id));
    }

    @Override
    public List<CustomOrder> findAll() {
        return repository.findAllByRemovedFalse().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<CustomOrder> findAllByClientId(UUID clientId) {
        return repository.findAllByClientIdAndRemovedFalse(clientId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new DomainValidationException("Entity not found: " + id));
        entity.setRemoved(true);
        repository.save(entity);
    }
}
