package ru.butenko.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "test_drive_cars")
@Getter
@Setter
public class TestDriveCarEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private CarEntity car;
}
