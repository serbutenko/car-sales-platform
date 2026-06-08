package ru.butenko.api.dto;

import java.time.Instant;
import java.util.UUID;

public record ComponentResponse(
        UUID id,
        UUID componentOptionId,
        int quantity,
        int reservedQuantity,
        int availableQuantity,
        Instant createdAt,
        Instant updatedAt,
        boolean removed
) {
}
