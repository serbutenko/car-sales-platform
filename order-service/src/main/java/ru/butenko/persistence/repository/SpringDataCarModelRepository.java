package ru.butenko.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.butenko.persistence.entity.CarModelEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataCarModelRepository extends JpaRepository<CarModelEntity, UUID> {

    Optional<CarModelEntity> findByIdAndRemovedFalse(UUID id);

    List<CarModelEntity> findAllByRemovedFalse();
}
