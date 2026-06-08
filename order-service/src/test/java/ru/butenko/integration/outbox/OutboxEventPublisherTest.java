package ru.butenko.integration.outbox;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.butenko.integration.kafka.KafkaOrderApprovalEventPublisher;
import ru.butenko.persistence.entity.OutboxEventEntity;
import ru.butenko.persistence.repository.SpringDataOutboxEventRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutboxEventPublisherTest {

    private final SpringDataOutboxEventRepository repository = mock(SpringDataOutboxEventRepository.class);
    private final KafkaOrderApprovalEventPublisher kafkaPublisher = mock(KafkaOrderApprovalEventPublisher.class);
    private final OutboxEventPublisher publisher = new OutboxEventPublisher(repository, kafkaPublisher);

    @Test
    void publishPendingEvents_shouldPublishAndMarkEventAsPublished() {
        ReflectionTestUtils.setField(publisher, "maxAttempts", 5);

        UUID orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        OutboxEventEntity event = new OutboxEventEntity();
        event.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        event.setEventId(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        event.setEventType("OrderSentForApproval");
        event.setAggregateId(orderId);
        event.setPayload("{\"traceId\":\"trace-1\"}");
        event.setCreatedAt(Instant.parse("2026-05-09T12:00:00Z"));
        event.setAttempts(0);
        event.setLastError("previous error");

        when(repository.findPendingBatch(5)).thenReturn(List.of(event));

        publisher.publishPendingEvents();

        verify(kafkaPublisher).publishPayload(orderId, "{\"traceId\":\"trace-1\"}");
        verify(repository).save(event);
        assertEquals(1, event.getAttempts());
        assertNull(event.getLastError());
        assertNotNull(event.getPublishedAt());
    }
}
