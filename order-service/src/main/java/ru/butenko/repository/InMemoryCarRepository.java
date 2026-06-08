package ru.butenko.repository;

import ru.butenko.application.abstractions.CarModelRepository;
import ru.butenko.application.abstractions.CarRepository;
import ru.butenko.domain.enums.DriveType;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.Car;
import ru.butenko.domain.model.CarFilter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCarRepository implements CarRepository {
    private final Map<UUID, Car> storage = new ConcurrentHashMap<>();

    @Override
    public Car findById(UUID id) {
        Car entity = storage.get(id);
        if (entity == null) {
            throw new DomainValidationException("Entity not found: " + id);
        }
        return entity;
    }

    @Override
    public Car save(Car entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }

    @Override
    public List<Car> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Car> findAll(CarFilter filter) {
        CarFilter f = filter == null ? new CarFilter() : filter;

        return storage.values().stream()
                .filter(c -> f.getMinPrice() == null || c.getPrice().compareTo(f.getMinPrice()) >= 0)
                .filter(c -> f.getMaxPrice() == null || c.getPrice().compareTo(f.getMaxPrice()) <= 0)
                .filter(c -> f.getBrand() == null || c.getBrand().equalsIgnoreCase(f.getBrand()))
                .filter(c -> f.getModelName() == null || c.getModelName().equalsIgnoreCase(f.getModelName()))
                .filter(c -> f.getBodyType() == null || c.getBodyType() == f.getBodyType())
                .filter(c -> f.getFuelType() == null || c.getFuelType() == f.getFuelType())
                .filter(c -> f.getTransmissionType() == null || c.getTransmissionType() == f.getTransmissionType())
                .filter(c -> f.getDriveType() == null || c.getDriveType() == f.getDriveType())
                .filter(c -> f.getColor() == null || c.getColor() == f.getColor())
                .filter(c -> f.getMinEnginePowerHp() == null || c.getEnginePowerHp() >= f.getMinEnginePowerHp().intValue())
                .filter(c -> f.getMaxEnginePowerHp() == null || c.getEnginePowerHp() <= f.getMaxEnginePowerHp().intValue())
                .filter(c -> f.getMinEngineVolume() == null || c.getEngineVolume().compareTo(f.getMinEngineVolume()) >= 0)
                .filter(c -> f.getMaxEngineVolume() == null || c.getEngineVolume().compareTo(f.getMaxEngineVolume()) <= 0)
                .toList();
    }
}
