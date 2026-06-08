package ru.butenko.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.butenko.api.dto.CreateCarRequest;
import ru.butenko.api.dto.CreateComponentRequest;
import ru.butenko.api.dto.CarResponse;
import ru.butenko.api.dto.ComponentResponse;
import ru.butenko.api.dto.UpdateCarRequest;
import ru.butenko.api.dto.UpdateComponentRequest;
import ru.butenko.api.exception.InventoryEntityNotFoundException;
import ru.butenko.api.mapper.InventoryDtoMapper;
import ru.butenko.domain.storage.CarStatus;
import ru.butenko.persistence.entity.CarEntity;
import ru.butenko.persistence.entity.ComponentEntity;
import ru.butenko.persistence.repository.CarRepository;
import ru.butenko.persistence.repository.ComponentRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryManagementService {
    private final CarRepository carRepository;
    private final ComponentRepository componentRepository;
    private final InventoryDtoMapper mapper;

    @Transactional
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public CarResponse createCar(CreateCarRequest request) {
        Instant now = Instant.now();
        CarEntity entity = new CarEntity();
        entity.setId(UUID.randomUUID());
        entity.setModelId(request.modelId());
        entity.setVin(request.vin());
        entity.setStatus(request.status() == null ? CarStatus.AVAILABLE : request.status());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setRemoved(false);

        return mapper.toCarResponse(carRepository.save(entity));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public List<CarResponse> findAllCars() {
        return carRepository.findAll()
                .stream()
                .filter(car -> !car.isRemoved())
                .map(mapper::toCarResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public CarResponse findCarById(UUID id) {
        return mapper.toCarResponse(getCar(id));
    }

    @Transactional
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public CarResponse updateCar(UUID id, UpdateCarRequest request) {
        CarEntity entity = getCar(id);
        entity.setModelId(request.modelId());
        entity.setVin(request.vin());
        entity.setStatus(request.status());
        entity.setUpdatedAt(Instant.now());

        return mapper.toCarResponse(carRepository.save(entity));
    }

    @Transactional
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public void deleteCar(UUID id) {
        CarEntity entity = getCar(id);
        entity.setRemoved(true);
        entity.setUpdatedAt(Instant.now());
        carRepository.save(entity);
    }

    @Transactional
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public ComponentResponse createComponent(CreateComponentRequest request) {
        Instant now = Instant.now();
        ComponentEntity entity = new ComponentEntity();
        entity.setId(UUID.randomUUID());
        entity.setComponentOptionId(request.componentOptionId());
        entity.setQuantity(request.quantity());
        entity.setReservedQuantity(0);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setRemoved(false);

        return mapper.toComponentResponse(componentRepository.save(entity));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public List<ComponentResponse> findAllComponents() {
        return componentRepository.findAll()
                .stream()
                .filter(component -> !component.isRemoved())
                .map(mapper::toComponentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public ComponentResponse findComponentById(UUID id) {
        return mapper.toComponentResponse(getComponent(id));
    }

    @Transactional
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public ComponentResponse updateComponent(UUID id, UpdateComponentRequest request) {
        ComponentEntity entity = getComponent(id);
        entity.setComponentOptionId(request.componentOptionId());
        entity.setQuantity(request.quantity());
        entity.setReservedQuantity(request.reservedQuantity());
        entity.setUpdatedAt(Instant.now());

        return mapper.toComponentResponse(componentRepository.save(entity));
    }

    @Transactional
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public void deleteComponent(UUID id) {
        ComponentEntity entity = getComponent(id);
        entity.setRemoved(true);
        entity.setUpdatedAt(Instant.now());
        componentRepository.save(entity);
    }

    private CarEntity getCar(UUID id) {
        return carRepository.findByIdAndRemovedFalse(id)
                .orElseThrow(() -> new InventoryEntityNotFoundException("Car", id));
    }

    private ComponentEntity getComponent(UUID id) {
        return componentRepository.findById(id)
                .filter(component -> !component.isRemoved())
                .orElseThrow(() -> new InventoryEntityNotFoundException("Component", id));
    }

}
