package ru.butenko.application.abstractions;

import ru.butenko.domain.model.Car;
import ru.butenko.domain.model.CarFilter;

import java.util.List;
import java.util.UUID;

public interface CarRepository extends BaseRepository<UUID, Car> {
    List<Car> findAll(CarFilter filter);
}
