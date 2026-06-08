package ru.butenko.api.mapper;

import org.springframework.stereotype.Component;
import ru.butenko.api.dto.request.CarFilterRequest;
import ru.butenko.api.dto.response.CarResponse;
import ru.butenko.domain.model.Car;
import ru.butenko.domain.model.CarFilter;

@Component
public class CarDtoMapper {
    public CarResponse toResponse(Car car) {
        return new CarResponse(
                car.getId(),
                car.getModelId(),
                car.getBrand(),
                car.getModelName(),
                car.getPrice(),
                car.getBodyType(),
                car.getFuelType(),
                car.getEnginePowerHp(),
                car.getEngineVolume(),
                car.getTransmissionType(),
                car.getDriveType(),
                car.getColor(),
                car.getStatus()
        );
    }

    public CarFilter toDomain(CarFilterRequest request) {
        CarFilter filter = new CarFilter();
        if (request == null) {
            return filter;
        }

        filter.setMinPrice(request.minPrice());
        filter.setMaxPrice(request.maxPrice());
        filter.setBrand(request.brand());
        filter.setModelName(request.modelName());
        filter.setMinEnginePowerHp(request.minEnginePowerHp());
        filter.setMaxEnginePowerHp(request.maxEnginePowerHp());
        filter.setMinEngineVolume(request.minEngineVolume());
        filter.setMaxEngineVolume(request.maxEngineVolume());
        filter.setTransmissionType(request.transmissionType());
        filter.setDriveType(request.driveType());
        filter.setBodyType(request.bodyType());
        filter.setFuelType(request.fuelType());
        filter.setColor(request.color());
        filter.setComponentOptionIds(request.componentOptionIds());

        return filter;
    }
}
