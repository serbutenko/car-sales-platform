package ru.butenko.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.butenko.application.abstractions.CarRepository;
import ru.butenko.application.abstractions.CustomOrderRepository;
import ru.butenko.application.abstractions.OrderApprovalEventPublisher;
import ru.butenko.application.abstractions.StockOrderRepository;
import ru.butenko.application.abstractions.UserRepository;
import ru.butenko.application.event.OrderSentForApprovalEvent;
import ru.butenko.domain.enums.UserRole;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.ConfigurationRequest;
import ru.butenko.domain.orders.custom.CustomOrder;
import ru.butenko.domain.orders.stock.StockOrder;
import ru.butenko.security.CurrentUserService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final CarRepository carRepo;
    private final UserRepository userRepo;

    private final StockOrderRepository stockOrderRepo;
    private final CustomOrderRepository customOrderRepo;
    private final ConfiguratorService configuratorService;
    private final CurrentUserService currentUserService;
    private final OrderApprovalEventPublisher orderApprovalEventPublisher;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public StockOrder createStockOrder(@NotNull UUID clientId, @NotNull UUID carId) {
        carRepo.findById(carId);
        UUID managerId = pickRandomManagerId();

        StockOrder order = new StockOrder(UUID.randomUUID(), clientId, managerId, carId);

        stockOrderRepo.save(order);
        return order;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public CustomOrder createCustomOrder(@NotNull UUID clientId, @NotNull ConfigurationRequest request) {
        UUID managerId = pickRandomManagerId();

        var config = configuratorService.build(request);
        CustomOrder order = new CustomOrder(
                UUID.randomUUID(),
                clientId,
                managerId,
                request.getModelId(),
                config
        );

        customOrderRepo.save(order);
        return order;
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or @orderSecurity.isStockOrderOwner(#orderId)")
    public StockOrder getStockOrder(UUID orderId) {
        return stockOrderRepo.findById(orderId);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public void stockApproveByManager(UUID orderId) {
        StockOrder o = getStockOrder(orderId);
        o.approveByManager();
        stockOrderRepo.save(o);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public void stockRequestPayment(UUID orderId) {
        StockOrder o = getStockOrder(orderId);
        o.requestPayment();
        stockOrderRepo.save(o);
    }

    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isStockOrderOwner(#orderId)")
    @Transactional
    public void stockPay(UUID orderId) {
        StockOrder o = getStockOrder(orderId);
        o.pay();
        stockOrderRepo.save(o);
        OrderSentForApprovalEvent event = stockApprovalEvent(o);
        log.info(
                "traceId={} orderId={} orderType={} eventId={} queued for warehouse approval",
                event.traceId(),
                event.orderId(),
                event.orderType(),
                event.eventId()
        );
        orderApprovalEventPublisher.publish(event);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public void stockReadyForDelivery(UUID orderId) {
        StockOrder o = getStockOrder(orderId);
        o.readyForDelivery();
        stockOrderRepo.save(o);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public void stockComplete(UUID orderId) {
        StockOrder o = getStockOrder(orderId);
        o.complete();
        stockOrderRepo.save(o);
    }

    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isStockOrderOwner(#orderId)")
    public void stockCancel(UUID orderId) {
        StockOrder o = getStockOrder(orderId);
        o.cancel();
        stockOrderRepo.save(o);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or @orderSecurity.isCustomOrderOwner(#orderId)")
    public CustomOrder getCustomOrder(UUID orderId) {
        return customOrderRepo.findById(orderId);
    }

    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public void customApprovedByStock(UUID orderId) {
        CustomOrder o = getCustomOrder(orderId);
        o.approveByStock();
        customOrderRepo.save(o);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public void customRequestPayment(UUID orderId) {
        CustomOrder o = getCustomOrder(orderId);
        o.requestPayment();
        customOrderRepo.save(o);
    }

    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isCustomOrderOwner(#orderId)")
    @Transactional
    public void customPay(UUID orderId) {
        CustomOrder o = getCustomOrder(orderId);
        o.pay();
        customOrderRepo.save(o);
        OrderSentForApprovalEvent event = customApprovalEvent(o);
        log.info(
                "traceId={} orderId={} orderType={} eventId={} queued for warehouse approval",
                event.traceId(),
                event.orderId(),
                event.orderType(),
                event.eventId()
        );
        orderApprovalEventPublisher.publish(event);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public void customWaitForDelivery(UUID orderId) {
        CustomOrder o = getCustomOrder(orderId);
        o.waitForDelivery();
        customOrderRepo.save(o);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public void customReadyForDelivery(UUID orderId) {
        CustomOrder o = getCustomOrder(orderId);
        o.readyForDelivery();
        customOrderRepo.save(o);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public void customComplete(UUID orderId) {
        CustomOrder o = getCustomOrder(orderId);
        o.complete();
        customOrderRepo.save(o);
    }

    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isCustomOrderOwner(#orderId)")
    public void customCancel(UUID orderId) {
        CustomOrder o = getCustomOrder(orderId);
        o.cancel();
        customOrderRepo.save(o);
    }

    private UUID pickRandomManagerId() {
        List<UUID> managers = userRepo.findAll().stream()
                .filter(u -> u.getRole() == UserRole.MANAGER)
                .map(u -> u.getId())
                .toList();
        if (managers.isEmpty()) {
            throw new DomainValidationException("No managers available");
        }

        return managers.get(ThreadLocalRandom.current().nextInt(managers.size()));
    }

    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public List<StockOrder> listStockOrders() {
        UUID currentUserId = currentUserService.getCurrentUserId();
        boolean canSeeAll = currentUserService.hasAnyRole("MANAGER", "ADMIN");
        if (canSeeAll) {
            return stockOrderRepo.findAll();
        }

        return stockOrderRepo.findAllByClientId(currentUserId);
    }

    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public List<CustomOrder> listCustomOrders() {
        UUID currentUserId = currentUserService.getCurrentUserId();
        boolean canSeeAll = currentUserService.hasAnyRole("MANAGER", "ADMIN");
        if (canSeeAll) {
            return customOrderRepo.findAll();
        }

        return customOrderRepo.findAllByClientId(currentUserId);
    }

    public void approveByStorage(UUID orderId, String orderType) {
        approveByStorage(orderId, orderType, null);
    }

    public void approveByStorage(UUID orderId, String orderType, String traceId) {
        log.info("traceId={} orderId={} orderType={} approved by warehouse", traceId, orderId, orderType);
        if ("STOCK".equals(orderType)) {
            StockOrder order = stockOrderRepo.findById(orderId);
            order.readyForDelivery();
            stockOrderRepo.save(order);
            return;
        }

        if ("CUSTOM".equals(orderType)) {
            CustomOrder order = customOrderRepo.findById(orderId);
            order.waitForDelivery();
            customOrderRepo.save(order);
            return;
        }

        throw new DomainValidationException("Unknown order type: " + orderType);
    }

    public void rejectByStorage(UUID orderId, String orderType) {
        rejectByStorage(orderId, orderType, null);
    }

    public void rejectByStorage(UUID orderId, String orderType, String traceId) {
        log.info("traceId={} orderId={} orderType={} rejected by warehouse", traceId, orderId, orderType);
        if ("STOCK".equals(orderType)) {
            StockOrder order = stockOrderRepo.findById(orderId);
            order.cancel();
            stockOrderRepo.save(order);
            return;
        }

        if ("CUSTOM".equals(orderType)) {
            CustomOrder order = customOrderRepo.findById(orderId);
            order.cancel();
            customOrderRepo.save(order);
            return;
        }

        throw new DomainValidationException("Unknown order type: " + orderType);
    }

    private OrderSentForApprovalEvent stockApprovalEvent(StockOrder order) {
        return new OrderSentForApprovalEvent(
                UUID.randomUUID(),
                UUID.randomUUID().toString(),
                order.getId(),
                "STOCK",
                order.getClientId(),
                order.getCarId(),
                null,
                List.of()
        );
    }

    private OrderSentForApprovalEvent customApprovalEvent(CustomOrder order) {
        List<UUID> requiredComponentIds = order.getConfiguration()
                .getSelectedOptions()
                .values()
                .stream()
                .map(detail -> detail.getId())
                .toList();

        return new OrderSentForApprovalEvent(
                UUID.randomUUID(),
                UUID.randomUUID().toString(),
                order.getId(),
                "CUSTOM",
                order.getClientId(),
                null,
                order.getModelId(),
                requiredComponentIds
        );
    }
}
