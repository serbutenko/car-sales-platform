package ru.butenko.api.mapper;

import org.springframework.stereotype.Component;
import ru.butenko.api.dto.response.AvailableCarResponse;
import ru.butenko.integration.grpc.StorageCar;

@Component
public class AvailableCarMapper {
    public AvailableCarResponse toResponse(StorageCar car) {
        return new AvailableCarResponse(
                car.id(),
                car.modelId(),
                car.vin(),
                car.status()
        );
    }
}
