package ru.butenko.integration.kafka;

import java.util.UUID;

public record OrderApprovalResultEvent(
        String eventType,
        UUID eventId,
        String traceId,
        UUID orderId,
        String orderType
) {
    public static OrderApprovalResultEvent approved(UUID orderId, String orderType) {
        return approved(orderId, orderType, null);
    }

    public static OrderApprovalResultEvent approved(UUID orderId, String orderType, String traceId) {
        return new OrderApprovalResultEvent(
                "OrderApproved",
                UUID.randomUUID(),
                resolveTraceId(traceId),
                orderId,
                orderType
        );
    }

    public static OrderApprovalResultEvent rejected(UUID orderId, String orderType) {
        return rejected(orderId, orderType, null);
    }

    public static OrderApprovalResultEvent rejected(UUID orderId, String orderType, String traceId) {
        return new OrderApprovalResultEvent(
                "OrderRejected",
                UUID.randomUUID(),
                resolveTraceId(traceId),
                orderId,
                orderType
        );
    }

    private static String resolveTraceId(String traceId) {
        if (traceId == null || traceId.isBlank()) {
            return UUID.randomUUID().toString();
        }

        return traceId;
    }
}
