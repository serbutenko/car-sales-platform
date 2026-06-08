package ru.butenko.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.butenko.domain.storage.CarStatus;
import ru.butenko.persistence.entity.ComponentEntity;
import ru.butenko.persistence.repository.CarRepository;
import ru.butenko.persistence.repository.ComponentRepository;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseInventoryService {
    private final CarRepository carRepository;
    private final ComponentRepository componentRepository;

    public boolean reserveFor(String orderType, UUID carId, List<UUID> requiredComponentIds) {
        if ("STOCK".equals(orderType)) {
            return reserveStockCar(carId);
        }
        if ("CUSTOM".equals(orderType)) {
            return reserveComponents(requiredComponentIds);
        }

        return false;
    }

    private boolean reserveStockCar(UUID carId) {
        if (carId == null) {
            return false;
        }

        return carRepository.findByIdAndRemovedFalse(carId)
                .filter(car -> car.getStatus() == CarStatus.AVAILABLE)
                .map(car -> {
                    car.setStatus(CarStatus.RESERVED);
                    car.setUpdatedAt(Instant.now());
                    carRepository.save(car);
                    return true;
                })
                .orElse(false);
    }

    private boolean reserveComponents(List<UUID> requiredComponentIds) {
        if (requiredComponentIds == null || requiredComponentIds.isEmpty()) {
            return false;
        }

        Map<UUID, Integer> requiredCounts = countRequiredComponents(requiredComponentIds);
        Map<ComponentEntity, Integer> componentsToReserve = new LinkedHashMap<>();
        for (Map.Entry<UUID, Integer> entry : requiredCounts.entrySet()) {
            ComponentEntity component = componentRepository
                    .findByComponentOptionIdAndRemovedFalse(entry.getKey())
                    .orElse(null);

            if (component == null || availableQuantity(component) < entry.getValue()) {
                return false;
            }
            componentsToReserve.put(component, entry.getValue());
        }

        Instant now = Instant.now();
        componentsToReserve.forEach((component, count) -> {
            component.setReservedQuantity(component.getReservedQuantity() + count);
            component.setUpdatedAt(now);
            componentRepository.save(component);
        });

        return true;
    }

    private Map<UUID, Integer> countRequiredComponents(List<UUID> componentIds) {
        Map<UUID, Integer> result = new LinkedHashMap<>();
        for (UUID componentId : componentIds) {
            result.merge(componentId, 1, Integer::sum);
        }

        return result;
    }

    private int availableQuantity(ComponentEntity component) {
        return component.getQuantity() - component.getReservedQuantity();
    }
}
