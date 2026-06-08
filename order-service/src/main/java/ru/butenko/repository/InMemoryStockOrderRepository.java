package ru.butenko.repository;

import ru.butenko.application.abstractions.StockOrderRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.orders.stock.StockOrder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStockOrderRepository implements StockOrderRepository {
    private final Map<UUID, StockOrder> storage = new ConcurrentHashMap<>();

    @Override
    public StockOrder save(StockOrder entity) {
        if (entity == null) throw new DomainValidationException("entity is null");
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public StockOrder findById(UUID id) {
        StockOrder entity = storage.get(id);
        if (entity == null) {
            throw new DomainValidationException("Entity not found: " + id);
        }
        return entity;
    }

    @Override
    public List<StockOrder> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<StockOrder> findAllByClientId(UUID clientId) {
        return storage.values().stream()
                .filter(order -> order.getClientId().equals(clientId))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }
}
