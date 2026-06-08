package ru.butenko.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.butenko.domain.enums.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cars")
@Getter
@Setter
public class CarEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private CarModelEntity model;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String modelName;

    @Column(precision = 19, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private BodyType bodyType;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Column
    private Integer enginePowerHp;

    @Column(precision = 10, scale = 2)
    private BigDecimal engineVolume;

    @Enumerated(EnumType.STRING)
    private TransmissionType transmissionType;

    @Enumerated(EnumType.STRING)
    private DriveType driveType;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;
}
