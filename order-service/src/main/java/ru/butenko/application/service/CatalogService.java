package ru.butenko.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.butenko.application.abstractions.CarRepository;
import ru.butenko.domain.model.Car;
import ru.butenko.domain.model.CarFilter;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogService {
    private final CarRepository carRepo;

    @PreAuthorize("hasRole('USER') or hasRole('WAREHOUSE_ADMIN') or hasRole('MANAGER') or hasRole('ADMIN')")
    public Car getCar(UUID id) {
        return carRepo.findById(id);
    }

    @PreAuthorize("hasRole('USER') or hasRole('WAREHOUSE_ADMIN') or hasRole('MANAGER') or hasRole('ADMIN')")
    public List<Car> listCars(CarFilter filter) {
        return carRepo.findAll(filter == null ? new CarFilter() : filter);
    }
}
