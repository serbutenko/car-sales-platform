package ru.butenko.integration.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.butenko.application.event.OrderSentForApprovalEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaOrderApprovalEventPublisher {
    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(OrderSentForApprovalEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            publishPayload(event.orderId(), payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize order approval event", e);
        }
    }

    public void publishPayload(UUID orderId, String payload) {
        OrderSentForApprovalEvent event;
        try {
            event = objectMapper.readValue(payload, OrderSentForApprovalEvent.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize order approval event", e);
        }
        MDC.put("traceId", event.traceId());
        try {
            kafkaTemplate.send(TOPIC, orderId.toString(), payload).get(10, TimeUnit.SECONDS);
            log.info(
                    "orderId={} orderType={} eventId={} published to Kafka topic={}",
                    event.orderId(),
                    event.orderType(),
                    event.eventId(),
                    TOPIC
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing order approval event", e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to publish order approval event", e);
        } finally {
            MDC.remove("traceId");
        }
    }
}
