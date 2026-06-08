package ru.butenko.api.mapper;

import org.springframework.stereotype.Component;
import ru.butenko.api.dto.AssemblyOrderResponse;
import ru.butenko.api.dto.CreateAssemblyOrderRequest;
import ru.butenko.domain.assembly.AssemblyOrderStatus;
import ru.butenko.persistence.entity.AssemblyOrderEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class AssemblyOrderMapper {

    public AssemblyOrderEntity toNewEntity(CreateAssemblyOrderRequest request) {
        Instant now = Instant.now();

        return new AssemblyOrderEntity(
                UUID.randomUUID(),
                request.sourceOrderId(),
                request.sourceOrderType(),
                request.carId(),
                request.modelId(),
                copyComponentIds(request.requiredComponentIds()),
                AssemblyOrderStatus.CREATED,
                request.warehouseAdminId(),
                now,
                now,
                false
        );
    }

    public AssemblyOrderResponse toResponse(AssemblyOrderEntity entity) {
        return new AssemblyOrderResponse(
                entity.getId(),
                entity.getSourceOrderId(),
                entity.getSourceOrderType(),
                entity.getCarId(),
                entity.getModelId(),
                copyComponentIds(entity.getRequiredComponentIds()),
                entity.getStatus(),
                entity.getWarehouseAdminId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isRemoved()
        );
    }

    public List<UUID> copyComponentIds(List<UUID> componentIds) {
        if (componentIds == null) {
            return List.of();
        }

        return List.copyOf(componentIds);
    }
}
