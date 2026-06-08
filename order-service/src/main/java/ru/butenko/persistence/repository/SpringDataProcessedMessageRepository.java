package ru.butenko.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.butenko.persistence.entity.ProcessedMessageEntity;

import java.util.UUID;

public interface SpringDataProcessedMessageRepository extends JpaRepository<ProcessedMessageEntity, UUID> {
    boolean existsByEventIdAndConsumerName(UUID eventId, String consumerName);
}
