package ru.butenko.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.butenko.api.dto.AssemblyOrderResponse;
import ru.butenko.api.dto.CreateAssemblyOrderRequest;
import ru.butenko.api.dto.UpdateAssemblyOrderRequest;
import ru.butenko.api.exception.AssemblyOrderNotFoundException;
import ru.butenko.api.mapper.AssemblyOrderMapper;
import ru.butenko.domain.assembly.AssemblyOrderStatus;
import ru.butenko.integration.kafka.OrderApprovalResultEvent;
import ru.butenko.integration.kafka.OrderApprovalResultPublisher;
import ru.butenko.persistence.entity.AssemblyOrderEntity;
import ru.butenko.persistence.repository.AssemblyOrderRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssemblyOrderService {
    private final AssemblyOrderRepository repository;
    private final OrderApprovalResultPublisher orderApprovalResultPublisher;
    private final WarehouseInventoryService warehouseInventoryService;
    private final AssemblyOrderMapper mapper;

    @Transactional
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public AssemblyOrderResponse create(CreateAssemblyOrderRequest request) {
        AssemblyOrderEntity entity = mapper.toNewEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public AssemblyOrderResponse createAndProcess(CreateAssemblyOrderRequest request) {
        AssemblyOrderEntity entity = mapper.toNewEntity(request);
        AssemblyOrderEntity saved = repository.save(entity);

        AssemblyOrderStatus previousStatus = saved.getStatus();
        boolean reserved = warehouseInventoryService.reserveFor(
                saved.getSourceOrderType(),
                saved.getCarId(),
                saved.getRequiredComponentIds()
        );

        saved.setStatus(reserved ? AssemblyOrderStatus.ASSEMBLED : AssemblyOrderStatus.FAIL);
        saved.setUpdatedAt(Instant.now());

        AssemblyOrderEntity processed = repository.save(saved);
        log.info(
                "traceId={} orderId={} orderType={} assemblyStatus={}",
                request.traceId(),
                processed.getSourceOrderId(),
                processed.getSourceOrderType(),
                processed.getStatus()
        );
        publishResultIfStatusChanged(previousStatus, processed, request.traceId());
        return mapper.toResponse(processed);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public List<AssemblyOrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .filter(order -> !order.isRemoved())
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public AssemblyOrderResponse findById(UUID id) {
        AssemblyOrderEntity entity = getExisting(id);
        return mapper.toResponse(entity);
    }

    @Transactional
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public AssemblyOrderResponse update(UUID id, UpdateAssemblyOrderRequest request) {
        AssemblyOrderEntity entity = getExisting(id);
        AssemblyOrderStatus previousStatus = entity.getStatus();

        entity.setSourceOrderId(request.sourceOrderId());
        entity.setSourceOrderType(request.sourceOrderType());
        entity.setCarId(request.carId());
        entity.setModelId(request.modelId());
        entity.setRequiredComponentIds(mapper.copyComponentIds(request.requiredComponentIds()));
        entity.setWarehouseAdminId(request.warehouseAdminId());
        entity.setStatus(request.status());
        entity.setUpdatedAt(Instant.now());

        AssemblyOrderEntity saved = repository.save(entity);
        publishResultIfStatusChanged(previousStatus, saved, null);
        return mapper.toResponse(saved);
    }

    @Transactional
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public AssemblyOrderResponse updateStatus(UUID id, AssemblyOrderStatus status) {
        AssemblyOrderEntity entity = getExisting(id);
        AssemblyOrderStatus previousStatus = entity.getStatus();

        entity.setStatus(status);
        entity.setUpdatedAt(Instant.now());

        AssemblyOrderEntity saved = repository.save(entity);
        publishResultIfStatusChanged(previousStatus, saved, null);
        return mapper.toResponse(saved);
    }

    @Transactional
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public AssemblyOrderResponse assignWarehouseAdmin(UUID id, UUID warehouseAdminId) {
        AssemblyOrderEntity entity = getExisting(id);

        entity.setWarehouseAdminId(warehouseAdminId);
        entity.setUpdatedAt(Instant.now());

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public void delete(UUID id) {
        AssemblyOrderEntity entity = getExisting(id);

        entity.setRemoved(true);
        entity.setUpdatedAt(Instant.now());

        repository.save(entity);
    }

    private AssemblyOrderEntity getExisting(UUID id) {
        return repository.findById(id)
                .filter(order -> !order.isRemoved())
                .orElseThrow(() -> new AssemblyOrderNotFoundException(id));
    }

    private void publishResultIfStatusChanged(
            AssemblyOrderStatus previousStatus,
            AssemblyOrderEntity entity,
            String traceId) {
        if (previousStatus == entity.getStatus()) {
            return;
        }

        if (entity.getStatus() == AssemblyOrderStatus.ASSEMBLED) {
            orderApprovalResultPublisher.publish(OrderApprovalResultEvent.approved(
                    entity.getSourceOrderId(),
                    entity.getSourceOrderType(),
                    traceId
            ));
        }

        if (entity.getStatus() == AssemblyOrderStatus.FAIL) {
            orderApprovalResultPublisher.publish(OrderApprovalResultEvent.rejected(
                    entity.getSourceOrderId(),
                    entity.getSourceOrderType(),
                    traceId
            ));
        }
    }
}
