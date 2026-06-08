package ru.butenko.repository;

import ru.butenko.application.abstractions.CustomOrderRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.orders.custom.CustomOrder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCustomOrderRepository implements CustomOrderRepository {
    private final Map<UUID, CustomOrder> storage = new ConcurrentHashMap<>();

    @Override
    public CustomOrder save(CustomOrder entity) {
        if (entity == null) throw new DomainValidationException("entity is null");
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public CustomOrder findById(UUID id) {
        CustomOrder entity = storage.get(id);
        if (entity == null) {
            throw new DomainValidationException("Entity not found: " + id);
        }
        return entity;
    }

    @Override
    public List<CustomOrder> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<CustomOrder> findAllByClientId(UUID clientId) {
        return storage.values().stream()
                .filter(order -> order.getClientId().equals(clientId))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }
}
