package ru.butenko.integration.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.butenko.persistence.entity.ProcessedMessageEntity;
import ru.butenko.persistence.repository.SpringDataProcessedMessageRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessedMessageService {
    private final SpringDataProcessedMessageRepository repository;

    @Transactional
    public void processIfNew(UUID eventId, String eventType, String consumerName, Runnable handler) {
        if (repository.existsByEventIdAndConsumerName(eventId, consumerName)) {
            return;
        }

        handler.run();

        ProcessedMessageEntity entity = new ProcessedMessageEntity();
        entity.setId(UUID.randomUUID());
        entity.setEventId(eventId);
        entity.setEventType(eventType);
        entity.setConsumerName(consumerName);
        entity.setProcessedAt(Instant.now());
        repository.save(entity);
    }
}
