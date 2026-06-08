package ru.butenko.api.dto;

import ru.butenko.domain.assembly.AssemblyOrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AssemblyOrderResponse(
        UUID id,
        UUID sourceOrderId,
        String sourceOrderType,
        UUID carId,
        UUID modelId,
        List<UUID> requiredComponentIds,
        AssemblyOrderStatus status,
        UUID wareHouseAdminId,
        Instant createdAt,
        Instant updatedAt,
        boolean removed
) {
}
