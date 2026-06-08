package ru.butenko.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class TestDriveRequest {
    private final UUID id;
    private final UUID clientId;
    private final UUID carId;
    private final LocalDateTime startAt;
}
