package ru.butenko.repository;

import ru.butenko.application.abstractions.UserRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {
    private final Map<UUID, User> storage = new ConcurrentHashMap<>();

    @Override
    public User save(User entity) {
        if (entity == null) throw new DomainValidationException("entity is null");
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public User findById(UUID id) {
        User entity = storage.get(id);
        if (entity == null) {
            throw new DomainValidationException("Entity not found: " + id);
        }
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) throw new DomainValidationException("id is null");
        storage.remove(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }
}
