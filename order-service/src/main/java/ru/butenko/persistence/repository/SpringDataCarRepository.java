package ru.butenko.persistence.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.butenko.persistence.entity.CarEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataCarRepository extends JpaRepository<CarEntity, UUID>, JpaSpecificationExecutor<CarEntity> {

    Optional<CarEntity> findByIdAndRemovedFalse(UUID id);

    List<CarEntity> findAllByRemovedFalse();

    @Override
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "model")
    List<CarEntity> findAll(Specification<CarEntity> spec);
}
