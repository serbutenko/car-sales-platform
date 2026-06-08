package ru.butenko.api.mapper;

import org.springframework.stereotype.Component;
import ru.butenko.api.dto.CarResponse;
import ru.butenko.api.dto.ComponentResponse;
import ru.butenko.persistence.entity.CarEntity;
import ru.butenko.persistence.entity.ComponentEntity;

@Component
public class InventoryDtoMapper {
    public CarResponse toCarResponse(CarEntity entity) {
        return new CarResponse(
                entity.getId(),
                entity.getModelId(),
                entity.getVin(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isRemoved()
        );
    }

    public ComponentResponse toComponentResponse(ComponentEntity entity) {
        return new ComponentResponse(
                entity.getId(),
                entity.getComponentOptionId(),
                entity.getQuantity(),
                entity.getReservedQuantity(),
                entity.getQuantity() - entity.getReservedQuantity(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isRemoved()
        );
    }
}
