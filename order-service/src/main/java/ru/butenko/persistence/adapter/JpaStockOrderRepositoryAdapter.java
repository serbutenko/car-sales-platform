package ru.butenko.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.butenko.application.abstractions.StockOrderRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.orders.custom.CustomOrder;
import ru.butenko.domain.orders.stock.StockOrder;
import ru.butenko.persistence.mapper.StockOrderEntityMapper;
import ru.butenko.persistence.repository.SpringDataStockOrderRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class JpaStockOrderRepositoryAdapter implements StockOrderRepository {

    private final SpringDataStockOrderRepository repository;
    private final StockOrderEntityMapper mapper;

    @Override
    public StockOrder save(StockOrder entity) {
        return mapper.toDomain(repository.save(mapper.toEntity(entity)));
    }

    @Override
    public StockOrder findById(UUID id) {
        return repository.findByIdAndRemovedFalse(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new DomainValidationException("Entity not found: " + id));
    }

    @Override
    public List<StockOrder> findAll() {
        return repository.findAllByRemovedFalse().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<StockOrder> findAllByClientId(UUID clientId) {
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
