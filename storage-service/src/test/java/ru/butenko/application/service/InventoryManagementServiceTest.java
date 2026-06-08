package ru.butenko.application.service;

import org.junit.jupiter.api.Test;
import ru.butenko.api.dto.CreateCarRequest;
import ru.butenko.api.dto.CreateComponentRequest;
import ru.butenko.api.mapper.InventoryDtoMapper;
import ru.butenko.domain.storage.CarStatus;
import ru.butenko.persistence.entity.CarEntity;
import ru.butenko.persistence.entity.ComponentEntity;
import ru.butenko.persistence.repository.CarRepository;
import ru.butenko.persistence.repository.ComponentRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventoryManagementServiceTest {

    private static final UUID CAR_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID MODEL_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID COMPONENT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private final CarRepository carRepository = mock(CarRepository.class);
    private final ComponentRepository componentRepository = mock(ComponentRepository.class);
    private final InventoryManagementService service = new InventoryManagementService(
            carRepository,
            componentRepository,
            new InventoryDtoMapper()
    );

    @Test
    void createCar_shouldSaveAvailableCarByDefault() {
        when(carRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.createCar(new CreateCarRequest(MODEL_ID, "VIN-1", null));

        assertEquals(MODEL_ID, response.modelId());
        assertEquals("VIN-1", response.vin());
        assertEquals(CarStatus.AVAILABLE, response.status());
        assertFalse(response.removed());
    }

    @Test
    void deleteCar_shouldMarkCarAsRemoved() {
        CarEntity car = car();
        when(carRepository.findByIdAndRemovedFalse(CAR_ID)).thenReturn(Optional.of(car));
        when(carRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.deleteCar(CAR_ID);

        assertEquals(true, car.isRemoved());
        verify(carRepository).save(car);
    }

    @Test
    void createComponent_shouldSaveComponentWithZeroReservedQuantity() {
        when(componentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.createComponent(new CreateComponentRequest(COMPONENT_ID, 5));

        assertEquals(COMPONENT_ID, response.componentOptionId());
        assertEquals(5, response.quantity());
        assertEquals(0, response.reservedQuantity());
        assertEquals(5, response.availableQuantity());
    }

    private CarEntity car() {
        CarEntity car = new CarEntity();
        car.setId(CAR_ID);
        car.setModelId(MODEL_ID);
        car.setVin("VIN-1");
        car.setStatus(CarStatus.AVAILABLE);
        car.setCreatedAt(Instant.parse("2026-05-09T12:00:00Z"));
        car.setUpdatedAt(Instant.parse("2026-05-09T12:00:00Z"));
        car.setRemoved(false);
        return car;
    }
}
