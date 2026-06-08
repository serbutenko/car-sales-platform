package ru.butenko.repository;

import ru.butenko.application.abstractions.ComponentOptionRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.ComponentOption;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryComponentOptionRepository implements ComponentOptionRepository {
    private final Map<UUID, ComponentOption> data = new ConcurrentHashMap<>();

    @Override
    public ComponentOption save(ComponentOption componentOption) {
        if  (componentOption == null) {
            throw new IllegalArgumentException("ComponentOption is null");
        }
        data.put(componentOption.getId(), componentOption);
        return componentOption;
    }

    @Override
    public ComponentOption findById(UUID id) {
        ComponentOption entity = data.get(id);
        if (entity == null) {
            throw new DomainValidationException("Entity not found: " + id);
        }
        return entity;
    }

    @Override
    public List<ComponentOption> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        if (id ==  null) {
            throw new IllegalArgumentException("id is null");
        }
        data.remove(id);
    }
}
