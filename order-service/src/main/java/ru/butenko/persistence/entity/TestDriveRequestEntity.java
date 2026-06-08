package ru.butenko.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_drive_requests")
@Getter
@Setter
public class TestDriveRequestEntity extends BaseEntity {
    @Column(name = "client_id", nullable = false)
    private java.util.UUID clientId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private CarEntity car;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;
}
