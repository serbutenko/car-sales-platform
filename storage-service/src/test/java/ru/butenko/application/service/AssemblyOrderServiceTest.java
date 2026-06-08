package ru.butenko.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.butenko.api.dto.CreateAssemblyOrderRequest;
import ru.butenko.api.dto.UpdateAssemblyOrderRequest;
import ru.butenko.api.exception.AssemblyOrderNotFoundException;
import ru.butenko.api.mapper.AssemblyOrderMapper;
import ru.butenko.integration.kafka.OrderApprovalResultPublisher;
import ru.butenko.domain.assembly.AssemblyOrderStatus;
import ru.butenko.persistence.entity.AssemblyOrderEntity;
import ru.butenko.persistence.repository.AssemblyOrderRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssemblyOrderServiceTest {

    private static final UUID ASSEMBLY_ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SOURCE_ORDER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID CAR_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID MODEL_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID COMPONENT_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID WAREHOUSE_ADMIN_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");

    @Mock
    private AssemblyOrderRepository repository;

    @Mock
    private OrderApprovalResultPublisher orderApprovalResultPublisher;

    @Mock
    private WarehouseInventoryService warehouseInventoryService;

    @Spy
    private AssemblyOrderMapper mapper = new AssemblyOrderMapper();

    @InjectMocks
    private AssemblyOrderService service;

    @Test
    void create_shouldSaveAssemblyOrderWithCreatedStatus() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var request = new CreateAssemblyOrderRequest(
                SOURCE_ORDER_ID,
                "CUSTOM",
                null,
                MODEL_ID,
                List.of(COMPONENT_ID),
                WAREHOUSE_ADMIN_ID,
                null
        );

        var response = service.create(request);

        assertEquals(SOURCE_ORDER_ID, response.sourceOrderId());
        assertEquals("CUSTOM", response.sourceOrderType());
        assertEquals(MODEL_ID, response.modelId());
        assertEquals(List.of(COMPONENT_ID), response.requiredComponentIds());
        assertEquals(AssemblyOrderStatus.CREATED, response.status());
        assertFalse(response.removed());
    }

    @Test
    void update_shouldReplaceEditableFields() {
        AssemblyOrderEntity existing = entity();
        when(repository.findById(ASSEMBLY_ORDER_ID)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var request = new UpdateAssemblyOrderRequest(
                SOURCE_ORDER_ID,
                "STOCK",
                CAR_ID,
                null,
                List.of(),
                WAREHOUSE_ADMIN_ID,
                AssemblyOrderStatus.ASSEMBLED
        );

        var response = service.update(ASSEMBLY_ORDER_ID, request);

        assertEquals("STOCK", response.sourceOrderType());
        assertEquals(CAR_ID, response.carId());
        assertEquals(AssemblyOrderStatus.ASSEMBLED, response.status());
        assertEquals(WAREHOUSE_ADMIN_ID, response.wareHouseAdminId());
        verify(orderApprovalResultPublisher).publish(argThat(event ->
                event.eventType().equals("OrderApproved")
                        && event.orderId().equals(SOURCE_ORDER_ID)
                        && event.orderType().equals("STOCK")
                        && event.eventId() != null
                        && event.traceId() != null
        ));
    }

    @Test
    void updateStatus_toFail_shouldPublishRejectedEvent() {
        AssemblyOrderEntity existing = entity();
        when(repository.findById(ASSEMBLY_ORDER_ID)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateStatus(ASSEMBLY_ORDER_ID, AssemblyOrderStatus.FAIL);

        verify(orderApprovalResultPublisher).publish(argThat(event ->
                event.eventType().equals("OrderRejected")
                        && event.orderId().equals(SOURCE_ORDER_ID)
                        && event.orderType().equals("CUSTOM")
                        && event.eventId() != null
                        && event.traceId() != null
        ));
    }

    @Test
    void createAndProcess_shouldAssembleOrder_whenInventoryIsReserved() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(warehouseInventoryService.reserveFor("CUSTOM", null, List.of(COMPONENT_ID))).thenReturn(true);

        var request = new CreateAssemblyOrderRequest(
                SOURCE_ORDER_ID,
                "CUSTOM",
                null,
                MODEL_ID,
                List.of(COMPONENT_ID),
                null,
                "trace-1"
        );

        var response = service.createAndProcess(request);

        assertEquals(AssemblyOrderStatus.ASSEMBLED, response.status());
        verify(orderApprovalResultPublisher).publish(argThat(event ->
                event.eventType().equals("OrderApproved")
                        && event.orderId().equals(SOURCE_ORDER_ID)
                        && event.orderType().equals("CUSTOM")
                        && event.traceId().equals("trace-1")
        ));
    }

    @Test
    void createAndProcess_shouldFailOrder_whenInventoryIsNotReserved() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(warehouseInventoryService.reserveFor("CUSTOM", null, List.of(COMPONENT_ID))).thenReturn(false);

        var request = new CreateAssemblyOrderRequest(
                SOURCE_ORDER_ID,
                "CUSTOM",
                null,
                MODEL_ID,
                List.of(COMPONENT_ID),
                null,
                "trace-2"
        );

        var response = service.createAndProcess(request);

        assertEquals(AssemblyOrderStatus.FAIL, response.status());
        verify(orderApprovalResultPublisher).publish(argThat(event ->
                event.eventType().equals("OrderRejected")
                        && event.orderId().equals(SOURCE_ORDER_ID)
                        && event.orderType().equals("CUSTOM")
                        && event.traceId().equals("trace-2")
        ));
    }

    @Test
    void assignWarehouseAdmin_shouldSetAssignee() {
        AssemblyOrderEntity existing = entity();
        when(repository.findById(ASSEMBLY_ORDER_ID)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.assignWarehouseAdmin(ASSEMBLY_ORDER_ID, WAREHOUSE_ADMIN_ID);

        assertEquals(WAREHOUSE_ADMIN_ID, response.wareHouseAdminId());
        verify(repository).save(argThat(entity -> WAREHOUSE_ADMIN_ID.equals(entity.getWarehouseAdminId())));
    }

    @Test
    void delete_shouldMarkOrderAsRemoved() {
        AssemblyOrderEntity existing = entity();
        when(repository.findById(ASSEMBLY_ORDER_ID)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<AssemblyOrderEntity> captor = ArgumentCaptor.forClass(AssemblyOrderEntity.class);

        service.delete(ASSEMBLY_ORDER_ID);

        verify(repository).save(captor.capture());
        assertEquals(true, captor.getValue().isRemoved());
    }

    @Test
    void findById_shouldThrowNotFound_whenOrderIsRemoved() {
        AssemblyOrderEntity existing = entity();
        existing.setRemoved(true);
        when(repository.findById(ASSEMBLY_ORDER_ID)).thenReturn(Optional.of(existing));

        assertThrows(AssemblyOrderNotFoundException.class, () -> service.findById(ASSEMBLY_ORDER_ID));
    }

    private AssemblyOrderEntity entity() {
        Instant now = Instant.parse("2026-05-09T12:00:00Z");
        return new AssemblyOrderEntity(
                ASSEMBLY_ORDER_ID,
                SOURCE_ORDER_ID,
                "CUSTOM",
                null,
                MODEL_ID,
                List.of(COMPONENT_ID),
                AssemblyOrderStatus.CREATED,
                null,
                now,
                now,
                false
        );
    }
}
