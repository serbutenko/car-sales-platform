package ru.butenko.integration.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.butenko.integration.kafka.OrderApprovalResultEvent;
import ru.butenko.persistence.repository.OutboxEventRepository;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutboxOrderApprovalResultPublisherTest {

    private final OutboxEventRepository repository = mock(OutboxEventRepository.class);
    private final OutboxOrderApprovalResultPublisher publisher = new OutboxOrderApprovalResultPublisher(
            repository,
            new ObjectMapper()
    );

    @Test
    void publish_shouldSaveEventToOutbox() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UUID orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        var event = OrderApprovalResultEvent.approved(orderId, "STOCK");

        publisher.publish(event);

        verify(repository).save(org.mockito.ArgumentMatchers.argThat(outboxEvent ->
                outboxEvent.getId() != null
                        && outboxEvent.getEventId().equals(event.eventId())
                        && outboxEvent.getEventType().equals("OrderApproved")
                        && outboxEvent.getAggregateId().equals(orderId)
                        && outboxEvent.getPayload().contains("\"eventType\":\"OrderApproved\"")
                        && outboxEvent.getCreatedAt() != null
                        && outboxEvent.getPublishedAt() == null
                        && outboxEvent.getAttempts() == 0
                        && outboxEvent.getLastError() == null
        ));
    }
}
