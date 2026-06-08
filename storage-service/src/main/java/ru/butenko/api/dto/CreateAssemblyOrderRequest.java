package ru.butenko.api.dto;

import java.util.List;
import java.util.UUID;

public record CreateAssemblyOrderRequest(
        UUID sourceOrderId,
        String sourceOrderType,
        UUID carId,
        UUID modelId,
        List<UUID> requiredComponentIds,
        UUID warehouseAdminId,
        String traceId
) {
}
