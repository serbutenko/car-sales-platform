package ru.butenko.repository;

import ru.butenko.application.abstractions.CarModelRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.CarModel;

import java.util.*;

public class InMemoryCarModelRepository implements CarModelRepository {
    private final Map<UUID, CarModel> data = new HashMap<>();

    @Override
    public CarModel save(CarModel carModel) {
        if (carModel == null) {
            throw new IllegalArgumentException("CarModel is null");
        }
        data.put(carModel.getId(), carModel);
        return carModel;
    }

    @Override
    public CarModel findById(UUID id) {
        CarModel entity = data.get(id);
        if (entity == null) {
            throw new DomainValidationException("Entity not found: " + id);
        }
        return entity;
    }

    @Override
    public List<CarModel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        data.remove(id);
    }
}
