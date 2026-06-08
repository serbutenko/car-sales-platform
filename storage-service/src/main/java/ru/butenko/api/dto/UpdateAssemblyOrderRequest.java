package ru.butenko.api.dto;

import ru.butenko.domain.assembly.AssemblyOrderStatus;

import java.util.List;
import java.util.UUID;

public record UpdateAssemblyOrderRequest(
        UUID sourceOrderId,
        String sourceOrderType,
        UUID carId,
        UUID modelId,
        List<UUID> requiredComponentIds,
        UUID warehouseAdminId,
        AssemblyOrderStatus status
) {
}
