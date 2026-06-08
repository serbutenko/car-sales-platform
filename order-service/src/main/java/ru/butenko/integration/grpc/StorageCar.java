package ru.butenko.integration.grpc;

import java.util.UUID;

public record StorageCar(
        UUID id,
        UUID modelId,
        String vin,
        String status
) {
}
