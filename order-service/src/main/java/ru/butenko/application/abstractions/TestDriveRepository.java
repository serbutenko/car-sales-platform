package ru.butenko.application.abstractions;

import ru.butenko.domain.model.TestDriveRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TestDriveRepository extends BaseRepository<UUID, TestDriveRequest> {
    List<TestDriveRequest> findAll(int page, int size);

    boolean existsByCarIdAndStartAt(UUID carId, LocalDateTime startAt);
}
