package ru.butenko.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.butenko.persistence.entity.TestDriveCarEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataTestDriveCarRepository extends JpaRepository<TestDriveCarEntity, UUID> {

    boolean existsByCar_IdAndRemovedFalse(UUID carId);

    Optional<TestDriveCarEntity> findByCar_IdAndRemovedFalse(UUID carId);

    List<TestDriveCarEntity> findAllByRemovedFalse();
}
