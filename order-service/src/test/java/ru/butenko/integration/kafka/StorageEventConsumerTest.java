package ru.butenko.integration.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.butenko.application.service.OrderService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class StorageEventConsumerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderService orderService = mock(OrderService.class);
    private final ProcessedMessageService processedMessageService = mock(ProcessedMessageService.class);
    private final StorageEventConsumer consumer = new StorageEventConsumer(
            objectMapper,
            orderService,
            processedMessageService
    );

    @Test
    void consumeApprovedEvent_shouldApproveOrderWithTraceId() throws Exception {
        doAnswer(invocation -> {
            invocation.getArgument(3, Runnable.class).run();
            return null;
        }).when(processedMessageService).processIfNew(any(), any(), any(), any());

        UUID eventId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID orderId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        var event = new OrderApprovalResultEvent(
                "OrderApproved",
                eventId,
                "trace-1",
                orderId,
                "STOCK"
        );

        consumer.consume(objectMapper.writeValueAsString(event));

        verify(processedMessageService).processIfNew(
                eq(eventId),
                eq("OrderApproved"),
                eq("order-service-storage-events"),
                any()
        );
        verify(orderService).approveByStorage(orderId, "STOCK", "trace-1");
    }
}
