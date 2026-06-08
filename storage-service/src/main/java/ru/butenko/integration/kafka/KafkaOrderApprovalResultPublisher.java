package ru.butenko.integration.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaOrderApprovalResultPublisher {
    private static final String TOPIC = "storage-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(OrderApprovalResultEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            publishPayload(event.orderId(), payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize order approval result event", e);
        }
    }

    public void publishPayload(UUID orderId, String payload) {
        OrderApprovalResultEvent event;
        try {
            event = objectMapper.readValue(payload, OrderApprovalResultEvent.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize order approval result event", e);
        }
        MDC.put("traceId", event.traceId());
        try {
            kafkaTemplate.send(TOPIC, orderId.toString(), payload).get(10, TimeUnit.SECONDS);
            log.info(
                    "orderId={} orderType={} eventId={} eventType={} published to Kafka topic={}",
                    event.orderId(),
                    event.eventType(),
                    event.eventId(),
                    event.eventType(),
                    TOPIC
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing order approval result event", e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to publish order approval result event", e);
        } finally {
            MDC.remove("traceId");
        }
    }
}
