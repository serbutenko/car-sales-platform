package ru.butenko.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.butenko.persistence.entity.TestDriveRequestEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataTestDriveRequestRepository extends JpaRepository<TestDriveRequestEntity, UUID> {

    Optional<TestDriveRequestEntity> findByIdAndRemovedFalse(UUID id);

    List<TestDriveRequestEntity> findAllByRemovedFalse();

    Page<TestDriveRequestEntity> findAllByRemovedFalse(Pageable pageable);

    boolean existsByCar_IdAndStartAtAndRemovedFalse(UUID carId, LocalDateTime startAt);
}
