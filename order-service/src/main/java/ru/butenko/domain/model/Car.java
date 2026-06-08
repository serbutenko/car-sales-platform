package ru.butenko.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.butenko.domain.enums.*;
import ru.butenko.domain.exception.DomainValidationException;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class Car {
    private final UUID id;
    private UUID modelId;
    private String brand;
    private String modelName;

    private BigDecimal price;
    private BodyType bodyType;
    private FuelType fuelType;
    private int enginePowerHp;
    private BigDecimal engineVolume;
    private TransmissionType transmissionType;
    private DriveType driveType;
    private Color color;
    private CarStatus status;

    public Car(
            UUID id,
            UUID modelId,
            String brand,
            String modelName,
            BigDecimal price,
            BodyType bodyType,
            FuelType fuelType,
            int enginePowerHp,
            BigDecimal engineVolume,
            TransmissionType transmissionType,
            DriveType driveType,
            Color color,
            CarStatus status
    ) {
        if (id == null) {
            throw new DomainValidationException("id is null");
        }
        if (modelId == null) {
            throw new DomainValidationException("modelId is null");
        }
        if (brand == null || brand.isBlank()) {
            throw new DomainValidationException("brand is blank");
        }
        if (modelName == null || modelName.isBlank()) {
            throw new DomainValidationException("modelName is blank");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("price is invalid");
        }
        if (enginePowerHp <= 0) {
            throw new DomainValidationException("enginePowerHp is invalid");
        }
        if (engineVolume == null || engineVolume.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("engineVolume is invalid");
        }
        if (bodyType == null || fuelType == null || transmissionType == null
                || driveType == null || color == null || status == null) {
            throw new DomainValidationException("car fields contains null");
        }
        this.id = id;
        this.modelId = modelId;
        this.brand = brand;
        this.modelName = modelName;
        this.price = price;
        this.bodyType = bodyType;
        this.fuelType = fuelType;
        this.enginePowerHp = enginePowerHp;
        this.engineVolume = engineVolume;
        this.transmissionType = transmissionType;
        this.driveType = driveType;
        this.color = color;
        this.status = status;
    }

    public void updatePrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("price is invalid");
        }
        this.price = price;
    }

    public void update(
            UUID modelId,
            String brand,
            String modelName,
            BigDecimal price,
            BodyType bodyType,
            FuelType fuelType,
            int enginePowerHp,
            BigDecimal engineVolume,
            TransmissionType transmissionType,
            DriveType driveType,
            Color color,
            CarStatus status
    ) {
        if (modelId == null) {
            throw new DomainValidationException("modelId is null");
        }
        if (brand == null || brand.isBlank()) {
            throw new DomainValidationException("brand is blank");
        }
        if (modelName == null || modelName.isBlank()) {
            throw new DomainValidationException("modelName is blank");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("price is invalid");
        }
        if (enginePowerHp <= 0) {
            throw new DomainValidationException("enginePowerHp is invalid");
        }
        if (engineVolume == null || engineVolume.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("engineVolume is invalid");
        }
        if (bodyType == null || fuelType == null || transmissionType == null
                || driveType == null || color == null || status == null) {
            throw new DomainValidationException("car fields contains null");
        }
        this.modelId = modelId;
        this.brand = brand;
        this.modelName = modelName;
        this.price = price;
        this.bodyType = bodyType;
        this.fuelType = fuelType;
        this.enginePowerHp = enginePowerHp;
        this.engineVolume = engineVolume;
        this.transmissionType = transmissionType;
        this.driveType = driveType;
        this.color = color;
        this.status = status;
    }

    public void updateStatus(CarStatus status) {
        this.status = status;
    }
}
