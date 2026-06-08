package ru.butenko.api.dto;

import ru.butenko.domain.storage.CarStatus;

import java.time.Instant;
import java.util.UUID;

public record CarResponse(
        UUID id,
        UUID modelId,
        String vin,
        CarStatus status,
        Instant createdAt,
        Instant updatedAt,
        boolean removed
) {
}
