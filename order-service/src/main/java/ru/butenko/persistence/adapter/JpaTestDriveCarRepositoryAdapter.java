package ru.butenko.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.butenko.application.abstractions.TestDriveCarRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.persistence.entity.TestDriveCarEntity;
import ru.butenko.persistence.repository.SpringDataCarRepository;
import ru.butenko.persistence.repository.SpringDataTestDriveCarRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class JpaTestDriveCarRepositoryAdapter implements TestDriveCarRepository {
    private final SpringDataTestDriveCarRepository repository;
    private final SpringDataCarRepository carRepository;

    @Override
    public void add(UUID carId) {
        if (repository.existsByCar_IdAndRemovedFalse(carId)) {
            return;
        }

        var carEntity = carRepository.findById(carId)
                .orElseThrow(() -> new DomainValidationException("Car not found: " + carId));
        TestDriveCarEntity entity = new TestDriveCarEntity();
        entity.setCar(carEntity);
        repository.save(entity);
    }

    @Override
    public void remove(UUID carId) {
        repository.findByCar_IdAndRemovedFalse(carId).ifPresent(entity -> {
            entity.setRemoved(true);
            repository.save(entity);
        });
    }

    @Override
    public boolean exists(UUID carId) {
        return repository.existsByCar_IdAndRemovedFalse(carId);
    }

    @Override
    public List<UUID> findAllCarIds() {
        return repository.findAllByRemovedFalse().stream()
                .map(entity -> entity.getCar().getId())
                .toList();
    }
}
