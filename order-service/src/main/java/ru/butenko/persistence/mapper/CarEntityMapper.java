package ru.butenko.persistence.mapper;

import org.springframework.stereotype.Component;
import ru.butenko.domain.model.Car;
import ru.butenko.persistence.entity.CarEntity;
import ru.butenko.persistence.entity.CarModelEntity;

@Component
public class CarEntityMapper {

    public Car toDomain(CarEntity entity) {
        return new Car(
                entity.getId(),
                entity.getModel().getId(),
                entity.getBrand(),
                entity.getModelName(),
                entity.getPrice(),
                entity.getBodyType(),
                entity.getFuelType(),
                entity.getEnginePowerHp() == null ? 0 : entity.getEnginePowerHp(),
                entity.getEngineVolume(),
                entity.getTransmissionType(),
                entity.getDriveType(),
                entity.getColor(),
                entity.getStatus()
        );
    }

    public CarEntity toEntity(Car domain, CarModelEntity modelEntity) {
        CarEntity entity = new CarEntity();
        entity.setId(domain.getId());
        entity.setModel(modelEntity);
        entity.setBrand(domain.getBrand());
        entity.setModelName(domain.getModelName());
        entity.setPrice(domain.getPrice());
        entity.setBodyType(domain.getBodyType());
        entity.setFuelType(domain.getFuelType());
        entity.setEnginePowerHp(domain.getEnginePowerHp());
        entity.setEngineVolume(domain.getEngineVolume());
        entity.setTransmissionType(domain.getTransmissionType());
        entity.setDriveType(domain.getDriveType());
        entity.setColor(domain.getColor());
        entity.setStatus(domain.getStatus());
        return entity;
    }
}