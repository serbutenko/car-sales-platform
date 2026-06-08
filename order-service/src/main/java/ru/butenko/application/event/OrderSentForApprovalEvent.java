package ru.butenko.application.event;

import java.util.List;
import java.util.UUID;

public record OrderSentForApprovalEvent(
        UUID eventId,
        String traceId,
        UUID orderId,
        String orderType,
        UUID clientId,
        UUID carId,
        UUID modelId,
        List<UUID> requiredComponentIds
) {
}
