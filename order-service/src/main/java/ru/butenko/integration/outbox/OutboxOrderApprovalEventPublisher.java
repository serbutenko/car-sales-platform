package ru.butenko.integration.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import ru.butenko.application.abstractions.OrderApprovalEventPublisher;
import ru.butenko.application.event.OrderSentForApprovalEvent;
import ru.butenko.persistence.entity.OutboxEventEntity;
import ru.butenko.persistence.repository.SpringDataOutboxEventRepository;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxOrderApprovalEventPublisher implements OrderApprovalEventPublisher {
    private static final String EVENT_TYPE = "OrderSentForApproval";

    private final SpringDataOutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(OrderSentForApprovalEvent event) {
        MDC.put("traceId", event.traceId());
        try {
            OutboxEventEntity entity = new OutboxEventEntity();
            entity.setId(UUID.randomUUID());
            entity.setEventId(event.eventId());
            entity.setEventType(EVENT_TYPE);
            entity.setAggregateId(event.orderId());
            entity.setPayload(objectMapper.writeValueAsString(event));
            entity.setCreatedAt(Instant.now());
            entity.setAttempts(0);

            repository.save(entity);
            log.info(
                    "orderId={} orderType={} eventId={} saved to outbox",
                    event.orderId(),
                    event.orderType(),
                    event.eventId()
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize order approval event", e);
        } finally {
            MDC.remove("traceId");
        }
    }
}
