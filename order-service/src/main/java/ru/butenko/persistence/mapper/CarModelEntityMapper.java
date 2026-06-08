package ru.butenko.persistence.mapper;

import org.springframework.stereotype.Component;
import ru.butenko.domain.model.CarModel;
import ru.butenko.persistence.entity.CarModelEntity;

@Component
public class CarModelEntityMapper {

    public CarModel toDomain(CarModelEntity entity) {
        return new CarModel(
                entity.getId(),
                entity.getName(),
                entity.getBrand(),
                entity.getPrice(),
                entity.getRequiredComponents()
        );
    }

    public CarModelEntity toEntity(CarModel domain) {
        CarModelEntity entity = new CarModelEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setBrand(domain.getBrand());
        entity.setPrice(domain.getPrice());
        entity.setRequiredComponents(domain.getRequiredComponents());
        return entity;
    }
}
