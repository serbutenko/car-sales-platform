package ru.butenko.integration.kafka;

import java.util.UUID;

public record OrderApprovalResultEvent(
        String eventType,
        UUID eventId,
        String traceId,
        UUID orderId,
        String orderType
) {
}
