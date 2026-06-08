package ru.butenko.integration.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.butenko.application.service.OrderService;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageEventConsumer {
    private static final String CONSUMER_NAME = "order-service-storage-events";

    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final ProcessedMessageService processedMessageService;

    @KafkaListener(topics = "storage-events", groupId = "order-service")
    public void consume(String message) {
        OrderApprovalResultEvent event;
        try {
            event = objectMapper.readValue(message, OrderApprovalResultEvent.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize storage event", e);
        }
        MDC.put("traceId", event.traceId());
        try {
            log.info(
                    "orderId={} orderType={} eventId={} eventType={} received from storage",
                    event.orderId(),
                    event.orderType(),
                    event.eventId(),
                    event.eventType()
            );
            processedMessageService.processIfNew(
                    event.eventId(),
                    event.eventType(),
                    CONSUMER_NAME,
                    () -> handle(event)
            );
        } finally {
            MDC.remove("traceId");
        }
    }

    private void handle(OrderApprovalResultEvent event) {
        if ("OrderApproved".equals(event.eventType())) {
            orderService.approveByStorage(event.orderId(), event.orderType(), event.traceId());
        } else if ("OrderRejected".equals(event.eventType())) {
            orderService.rejectByStorage(event.orderId(), event.orderType(), event.traceId());
        }
    }
}
