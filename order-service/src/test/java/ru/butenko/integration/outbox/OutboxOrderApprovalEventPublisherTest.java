package ru.butenko.integration.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.butenko.application.event.OrderSentForApprovalEvent;
import ru.butenko.persistence.entity.OutboxEventEntity;
import ru.butenko.persistence.repository.SpringDataOutboxEventRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutboxOrderApprovalEventPublisherTest {

    private final SpringDataOutboxEventRepository repository = mock(SpringDataOutboxEventRepository.class);
    private final OutboxOrderApprovalEventPublisher publisher = new OutboxOrderApprovalEventPublisher(
            repository,
            new ObjectMapper()
    );

    @Test
    void publish_shouldSaveEventToOutbox() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UUID eventId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID orderId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID clientId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID carId = UUID.fromString("44444444-4444-4444-4444-444444444444");

        var event = new OrderSentForApprovalEvent(
                eventId,
                "trace-1",
                orderId,
                "STOCK",
                clientId,
                carId,
                null,
                List.of()
        );

        publisher.publish(event);

        verify(repository).save(org.mockito.ArgumentMatchers.argThat(outboxEvent ->
                outboxEvent.getId() != null
                        && outboxEvent.getEventId().equals(eventId)
                        && outboxEvent.getEventType().equals("OrderSentForApproval")
                        && outboxEvent.getAggregateId().equals(orderId)
                        && outboxEvent.getPayload().contains("\"orderType\":\"STOCK\"")
                        && outboxEvent.getCreatedAt() != null
                        && outboxEvent.getPublishedAt() == null
                        && outboxEvent.getAttempts() == 0
                        && outboxEvent.getLastError() == null
        ));
    }
}
