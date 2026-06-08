package ru.butenko.repository;

import ru.butenko.application.abstractions.TestDriveCarRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTestDriveCarRepository implements TestDriveCarRepository {
    private final Set<UUID> storage = ConcurrentHashMap.newKeySet();

    @Override
    public void add(UUID carId) {
        storage.add(carId);
    }

    @Override
    public void remove(UUID carId) {
        storage.remove(carId);
    }

    @Override
    public boolean exists(UUID carId) {
        return storage.contains(carId);
    }

    @Override
    public List<UUID> findAllCarIds() {
        return new ArrayList<>(storage);
    }
}
