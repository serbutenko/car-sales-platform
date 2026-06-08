package ru.butenko.application.service;

import org.junit.jupiter.api.Test;
import ru.butenko.domain.storage.CarStatus;
import ru.butenko.persistence.entity.CarEntity;
import ru.butenko.persistence.entity.ComponentEntity;
import ru.butenko.persistence.repository.CarRepository;
import ru.butenko.persistence.repository.ComponentRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WarehouseInventoryServiceTest {

    private static final UUID CAR_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID MODEL_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID COMPONENT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private final CarRepository carRepository = mock(CarRepository.class);
    private final ComponentRepository componentRepository = mock(ComponentRepository.class);
    private final WarehouseInventoryService service = new WarehouseInventoryService(carRepository, componentRepository);

    @Test
    void reserveFor_stockOrder_shouldReserveAvailableCar() {
        CarEntity car = car(CarStatus.AVAILABLE);
        when(carRepository.findByIdAndRemovedFalse(CAR_ID)).thenReturn(Optional.of(car));

        boolean result = service.reserveFor("STOCK", CAR_ID, List.of());

        assertTrue(result);
        assertEquals(CarStatus.RESERVED, car.getStatus());
        verify(carRepository).save(car);
    }

    @Test
    void reserveFor_stockOrder_shouldFail_whenCarIsNotAvailable() {
        CarEntity car = car(CarStatus.RESERVED);
        when(carRepository.findByIdAndRemovedFalse(CAR_ID)).thenReturn(Optional.of(car));

        boolean result = service.reserveFor("STOCK", CAR_ID, List.of());

        assertFalse(result);
        assertEquals(CarStatus.RESERVED, car.getStatus());
    }

    @Test
    void reserveFor_customOrder_shouldReserveComponents_whenEnoughQuantity() {
        ComponentEntity component = component(5, 2);
        when(componentRepository.findByComponentOptionIdAndRemovedFalse(COMPONENT_ID))
                .thenReturn(Optional.of(component));

        boolean result = service.reserveFor("CUSTOM", null, List.of(COMPONENT_ID));

        assertTrue(result);
        assertEquals(3, component.getReservedQuantity());
        verify(componentRepository).save(component);
    }

    @Test
    void reserveFor_customOrder_shouldFail_whenComponentIsMissing() {
        when(componentRepository.findByComponentOptionIdAndRemovedFalse(COMPONENT_ID))
                .thenReturn(Optional.empty());

        boolean result = service.reserveFor("CUSTOM", null, List.of(COMPONENT_ID));

        assertFalse(result);
    }

    private CarEntity car(CarStatus status) {
        CarEntity car = new CarEntity();
        car.setId(CAR_ID);
        car.setModelId(MODEL_ID);
        car.setVin("VIN-1");
        car.setStatus(status);
        car.setCreatedAt(Instant.parse("2026-05-09T12:00:00Z"));
        car.setUpdatedAt(Instant.parse("2026-05-09T12:00:00Z"));
        car.setRemoved(false);
        return car;
    }

    private ComponentEntity component(int quantity, int reservedQuantity) {
        ComponentEntity component = new ComponentEntity();
        component.setId(UUID.randomUUID());
        component.setComponentOptionId(COMPONENT_ID);
        component.setQuantity(quantity);
        component.setReservedQuantity(reservedQuantity);
        component.setCreatedAt(Instant.parse("2026-05-09T12:00:00Z"));
        component.setUpdatedAt(Instant.parse("2026-05-09T12:00:00Z"));
        component.setRemoved(false);
        return component;
    }
}
