package ru.butenko.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.butenko.persistence.entity.ComponentOptionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataComponentOptionRepository extends JpaRepository<ComponentOptionEntity, UUID> {

    Optional<ComponentOptionEntity> findByIdAndRemovedFalse(UUID id);

    List<ComponentOptionEntity> findAllByRemovedFalse();
}
