package ru.butenko.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.butenko.domain.storage.CarStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "storage_cars")
@Getter
@Setter
public class CarEntity {
    @Id
    private UUID id;

    @Column(name = "model_id", nullable = false)
    private UUID modelId;

    @Column(nullable = false)
    private String vin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(nullable = false)
    private boolean removed;
}
