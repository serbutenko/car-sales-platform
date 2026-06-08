package ru.butenko.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.butenko.persistence.entity.CustomOrderEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataCustomOrderRepository extends JpaRepository<CustomOrderEntity, UUID> {

    Optional<CustomOrderEntity> findByIdAndRemovedFalse(UUID id);

    List<CustomOrderEntity> findAllByRemovedFalse();

    List<CustomOrderEntity> findAllByClientIdAndRemovedFalse(UUID clientId);
}
