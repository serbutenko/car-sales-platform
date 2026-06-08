package ru.butenko.integration.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.butenko.api.dto.CreateAssemblyOrderRequest;
import ru.butenko.application.service.AssemblyOrderService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private static final String CONSUMER_NAME = "storage-service-order-events";
    private static final UUID UNASSIGNED_WAREHOUSE_ADMIN_ID = null;

    private final ObjectMapper objectMapper;
    private final AssemblyOrderService assemblyOrderService;
    private final ProcessedMessageService processedMessageService;

    @KafkaListener(topics = "order-events", groupId = "storage-service")
    public void consume(String message) {
        OrderSentForApprovalEvent event;
        try {
            event = objectMapper.readValue(message, OrderSentForApprovalEvent.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize order event", e);
        }
        MDC.put("traceId", event.traceId());
        try {
            log.info(
                    "orderId={} orderType={} eventId={} received for warehouse processing",
                    event.orderId(),
                    event.orderType(),
                    event.eventId()
            );
            processedMessageService.processIfNew(
                    event.eventId(),
                    "OrderSentForApproval",
                    CONSUMER_NAME,
                    () -> handle(event)
            );
        } finally {
            MDC.remove("traceId");
        }
    }

    private void handle(OrderSentForApprovalEvent event) {
        log.info(
                "traceId={} orderId={} orderType={} creating assembly order",
                event.traceId(),
                event.orderId(),
                event.orderType()
        );
        var request = new CreateAssemblyOrderRequest(
                event.orderId(),
                event.orderType(),
                event.carId(),
                event.modelId(),
                event.requiredComponentIds(),
                UNASSIGNED_WAREHOUSE_ADMIN_ID,
                event.traceId()
        );
        assemblyOrderService.createAndProcess(request);
    }
}
