package ru.butenko.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.butenko.persistence.entity.ComponentEntity;

import java.util.Optional;
import java.util.UUID;

public interface ComponentRepository extends JpaRepository<ComponentEntity, UUID> {
    Optional<ComponentEntity> findByComponentOptionIdAndRemovedFalse(UUID componentOptionId);
}
