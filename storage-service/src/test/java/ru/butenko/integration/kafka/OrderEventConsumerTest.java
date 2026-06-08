package ru.butenko.integration.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.butenko.api.dto.CreateAssemblyOrderRequest;
import ru.butenko.application.service.AssemblyOrderService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderEventConsumerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AssemblyOrderService assemblyOrderService = mock(AssemblyOrderService.class);
    private final ProcessedMessageService processedMessageService = mock(ProcessedMessageService.class);
    private final OrderEventConsumer consumer = new OrderEventConsumer(
            objectMapper,
            assemblyOrderService,
            processedMessageService
    );

    @Test
    void consume_shouldCreateAssemblyOrderWithTraceId() throws Exception {
        doAnswer(invocation -> {
            invocation.getArgument(3, Runnable.class).run();
            return null;
        }).when(processedMessageService).processIfNew(any(), any(), any(), any());

        UUID eventId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID orderId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID clientId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID modelId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        UUID componentId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        var event = new OrderSentForApprovalEvent(
                eventId,
                "trace-1",
                orderId,
                "CUSTOM",
                clientId,
                null,
                modelId,
                List.of(componentId)
        );

        consumer.consume(objectMapper.writeValueAsString(event));

        verify(processedMessageService).processIfNew(
                eq(eventId),
                eq("OrderSentForApproval"),
                eq("storage-service-order-events"),
                any()
        );

        ArgumentCaptor<CreateAssemblyOrderRequest> requestCaptor =
                ArgumentCaptor.forClass(CreateAssemblyOrderRequest.class);
        verify(assemblyOrderService).createAndProcess(requestCaptor.capture());

        CreateAssemblyOrderRequest request = requestCaptor.getValue();
        assertEquals(orderId, request.sourceOrderId());
        assertEquals("CUSTOM", request.sourceOrderType());
        assertEquals(modelId, request.modelId());
        assertEquals(List.of(componentId), request.requiredComponentIds());
        assertEquals("trace-1", request.traceId());
        assertNull(request.warehouseAdminId());
    }
}
