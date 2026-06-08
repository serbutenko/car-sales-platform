package ru.butenko.api.dto;

import ru.butenko.domain.storage.CarStatus;

import java.util.UUID;

public record UpdateCarRequest(
        UUID modelId,
        String vin,
        CarStatus status
) {
}
