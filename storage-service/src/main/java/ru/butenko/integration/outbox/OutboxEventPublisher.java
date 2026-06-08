package ru.butenko.integration.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.butenko.integration.kafka.KafkaOrderApprovalResultPublisher;
import ru.butenko.persistence.entity.OutboxEventEntity;
import ru.butenko.persistence.repository.OutboxEventRepository;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {
    private final OutboxEventRepository repository;
    private final KafkaOrderApprovalResultPublisher kafkaPublisher;

    @Value("${app.outbox.max-attempts:5}")
    private int maxAttempts;

    @Scheduled(fixedDelayString = "${app.outbox.publish-delay-ms:5000}")
    @Transactional
    public void publishPendingEvents() {
        for (OutboxEventEntity event : repository.findPendingBatch(maxAttempts)) {
            try {
                kafkaPublisher.publishPayload(event.getAggregateId(), event.getPayload());
                event.setAttempts(event.getAttempts() + 1);
                event.setLastError(null);
                event.setPublishedAt(Instant.now());
            } catch (Exception exception) {
                event.setAttempts(event.getAttempts() + 1);
                event.setLastError(exception.getMessage());
            }
            repository.save(event);
        }
    }
}
