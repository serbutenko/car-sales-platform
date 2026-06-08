package ru.butenko.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.butenko.persistence.entity.OutboxEventEntity;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {
    default List<OutboxEventEntity> findPendingBatch(int maxAttempts) {
        return findTop50ByPublishedAtIsNullAndAttemptsLessThanOrderByCreatedAtAsc(maxAttempts);
    }

    List<OutboxEventEntity> findTop50ByPublishedAtIsNullAndAttemptsLessThanOrderByCreatedAtAsc(int maxAttempts);
}
