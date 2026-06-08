package ru.butenko.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.butenko.domain.storage.CarStatus;
import ru.butenko.persistence.entity.CarEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarRepository extends JpaRepository<CarEntity, UUID> {
    Optional<CarEntity> findByIdAndRemovedFalse(UUID id);

    List<CarEntity> findAllByStatusAndRemovedFalse(CarStatus status);

    Optional<CarEntity> findByIdAndStatusAndRemovedFalse(UUID id, CarStatus status);
}
