package ru.butenko.repository;

import ru.butenko.application.abstractions.TestDriveRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.TestDriveRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTestDriveRepository implements TestDriveRepository {
    private final Map<UUID, TestDriveRequest> storage = new ConcurrentHashMap<>();

    @Override
    public TestDriveRequest save(TestDriveRequest entity) {
        if (entity == null) {
            throw new DomainValidationException("entity is null");
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public TestDriveRequest findById(UUID id) {
        TestDriveRequest entity = storage.get(id);
        if (entity == null) {
            throw new DomainValidationException("Entity not found: " + id);
        }
        return entity;
    }

    @Override
    public List<TestDriveRequest> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) throw new DomainValidationException("id is null");
        storage.remove(id);
    }


    @Override
    public List<TestDriveRequest> findAll(int page, int size) {
        return storage.values().stream()
                .sorted(Comparator.comparing(TestDriveRequest::getStartAt))
                .skip((long)page * size)
                .limit(size)
                .toList();
    }

    @Override
    public boolean existsByCarIdAndStartAt(UUID carId, LocalDateTime startAt) {
        return storage.values().stream()
                .anyMatch(r -> r.getCarId().equals(carId) && r.getStartAt().equals(startAt));
    }
}
