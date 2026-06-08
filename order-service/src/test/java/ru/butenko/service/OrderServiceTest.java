package ru.butenko.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.butenko.application.components.*;
import ru.butenko.application.abstractions.OrderApprovalEventPublisher;
import ru.butenko.application.service.ConfiguratorService;
import ru.butenko.application.service.ConfiguratorValidator;
import ru.butenko.application.service.OrderService;
import ru.butenko.application.service.PriceCalculator;
import ru.butenko.domain.enums.BodyType;
import ru.butenko.domain.enums.CarStatus;
import ru.butenko.domain.enums.Color;
import ru.butenko.domain.enums.CustomOrderStatus;
import ru.butenko.domain.enums.DriveType;
import ru.butenko.domain.enums.FuelType;
import ru.butenko.domain.enums.StockOrderStatus;
import ru.butenko.domain.enums.TransmissionType;
import ru.butenko.domain.enums.UserRole;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.Car;
import ru.butenko.domain.model.CarConfiguration;
import ru.butenko.domain.model.User;
import ru.butenko.domain.orders.custom.CustomOrder;
import ru.butenko.domain.orders.stock.StockOrder;
import ru.butenko.repository.*;
import ru.butenko.security.CurrentUserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OrderServiceTest {
    private InMemoryCarRepository carRepo;
    private InMemoryUserRepository userRepo;
    private InMemoryStockOrderRepository stockOrderRepo;
    private InMemoryCustomOrderRepository customOrderRepo;

    private InMemoryCarModelRepository modelRepo;
    private InMemoryComponentOptionRepository optionRepo;
    private ConfiguratorService configurator;
    private OrderService orderService;
    private CurrentUserService currentUserService;
    private OrderApprovalEventPublisher orderApprovalEventPublisher;

    private UUID clientId;
    private UUID managerId;
    private UUID carId;

    @BeforeEach
    void setUp() {
        carRepo = new InMemoryCarRepository();
        userRepo = new InMemoryUserRepository();
        stockOrderRepo = new InMemoryStockOrderRepository();
        customOrderRepo = new InMemoryCustomOrderRepository();

        modelRepo = new InMemoryCarModelRepository();
        optionRepo = new InMemoryComponentOptionRepository();
        var registry = new DetailFactoryRegistry(List.of(
                new WheelsDetailCreator(optionRepo),
                new TransmissionDetailCreator(optionRepo),
                new SteeringWheelDetailCreator(optionRepo),
                new InteriorDetailCreator(optionRepo)
        ));
        var validator = new ConfiguratorValidator();
        var priceCalculator = new PriceCalculator();
        configurator = new ConfiguratorService(modelRepo, registry, validator, priceCalculator);
        currentUserService = new TestCurrentUserService();
        orderApprovalEventPublisher = mock(OrderApprovalEventPublisher.class);
        orderService = new OrderService(
                carRepo, userRepo,
                stockOrderRepo, customOrderRepo,
                configurator, currentUserService,
                orderApprovalEventPublisher
        );
        clientId = UUID.randomUUID();
        managerId = UUID.randomUUID();
        userRepo.save(new User(managerId, "Manager #1", UserRole.MANAGER));

        carId = UUID.randomUUID();

        Car car = new Car(
                carId,
                UUID.randomUUID(),
                "BMW",
                "320i",
                BigDecimal.valueOf(3_000_000),
                BodyType.SEDAN,
                FuelType.GASOLINE,
                184,
                BigDecimal.valueOf(2.0),
                TransmissionType.AUTOMATIC,
                DriveType.RWD,
                Color.BLACK,
                CarStatus.AVAILABLE
        );
        carRepo.save(car);

    }

    @Test
    void createStockOrder_shouldCreateOrderWithoutChangingCarStatus() {
        StockOrder order = orderService.createStockOrder(clientId, carId);

        assertNotNull(order.getId());
        assertEquals(clientId, order.getClientId());
        assertEquals(carId, order.getCarId());
        assertNotNull(order.getManagerId());
        assertEquals(StockOrderStatus.ISSUED, order.getStatus());

        Car car = carRepo.findById(carId);
        assertEquals(CarStatus.AVAILABLE, car.getStatus());
    }

    @Test
    void stockCancel_shouldCancelOrderWithoutChangingCarStatus() {
        StockOrder order = orderService.createStockOrder(clientId, carId);

        orderService.stockCancel(order.getId());

        Car car = carRepo.findById(carId);
        assertEquals(CarStatus.AVAILABLE, car.getStatus());

        StockOrder saved = stockOrderRepo.findById(order.getId());
        assertEquals(StockOrderStatus.CANCELLED, saved.getStatus());
    }

    @Test
    void stockPay_shouldPublishOrderSentForApprovalEvent() {
        StockOrder order = orderService.createStockOrder(clientId, carId);
        orderService.stockApproveByManager(order.getId());
        orderService.stockRequestPayment(order.getId());

        orderService.stockPay(order.getId());

        verify(orderApprovalEventPublisher).publish(org.mockito.ArgumentMatchers.argThat(event ->
                event.orderId().equals(order.getId())
                        && event.orderType().equals("STOCK")
                        && event.clientId().equals(clientId)
                        && event.carId().equals(carId)
                        && event.modelId() == null
                        && event.requiredComponentIds().isEmpty()
                        && event.eventId() != null
                        && event.traceId() != null
        ));
    }

    @Test
    void stockComplete_shouldCompleteOrderWithoutChangingCarStatus() {
        StockOrder order = orderService.createStockOrder(clientId, carId);
        orderService.stockApproveByManager(order.getId());
        orderService.stockRequestPayment(order.getId());
        orderService.stockPay(order.getId());
        orderService.stockReadyForDelivery(order.getId());
        orderService.stockComplete(order.getId());

        StockOrder savedOrder = stockOrderRepo.findById(order.getId());
        assertEquals(StockOrderStatus.COMPLETED, savedOrder.getStatus());

        Car savedCar = carRepo.findById(carId);
        assertEquals(CarStatus.AVAILABLE, savedCar.getStatus());
    }

    @Test
    void approveByStorage_shouldMovePaidStockOrderToReadyForDelivery() {
        StockOrder order = orderService.createStockOrder(clientId, carId);
        orderService.stockApproveByManager(order.getId());
        orderService.stockRequestPayment(order.getId());
        orderService.stockPay(order.getId());

        orderService.approveByStorage(order.getId(), "STOCK");

        StockOrder savedOrder = stockOrderRepo.findById(order.getId());
        assertEquals(StockOrderStatus.READY_FOR_DELIVERY, savedOrder.getStatus());
    }

    @Test
    void rejectByStorage_shouldCancelPaidStockOrder() {
        StockOrder order = orderService.createStockOrder(clientId, carId);
        orderService.stockApproveByManager(order.getId());
        orderService.stockRequestPayment(order.getId());
        orderService.stockPay(order.getId());

        orderService.rejectByStorage(order.getId(), "STOCK");

        StockOrder savedOrder = stockOrderRepo.findById(order.getId());
        assertEquals(StockOrderStatus.CANCELLED, savedOrder.getStatus());
    }

    @Test
    void approveByStorage_shouldMovePaidCustomOrderToWaitingForDelivery() {
        UUID orderId = UUID.randomUUID();
        UUID modelId = UUID.randomUUID();
        CustomOrder order = new CustomOrder(
                orderId,
                clientId,
                managerId,
                modelId,
                new CarConfiguration(modelId, Map.of(), BigDecimal.ZERO)
        );
        order.approveByStock();
        order.requestPayment();
        order.pay();
        customOrderRepo.save(order);

        orderService.approveByStorage(orderId, "CUSTOM");

        CustomOrder savedOrder = customOrderRepo.findById(orderId);
        assertEquals(CustomOrderStatus.WAITING_FOR_DELIVERY, savedOrder.getStatus());
    }

    @Test
    void listStockOrders_whenTwoUsersHaveOrders_shouldReturnOnlyCurrentUserOrders() {
        UUID secondClientId = UUID.randomUUID();

        StockOrder firstUserOrder = orderService.createStockOrder(clientId, carId);
        UUID secondCarId = UUID.randomUUID();
        carRepo.save(createAvailableCar(secondCarId));
        StockOrder secondUserOrder = orderService.createStockOrder(secondClientId, secondCarId);

        ((TestCurrentUserService) currentUserService).setCurrentUser(clientId, false);
        List<StockOrder> firstUserOrders = orderService.listStockOrders();
        ((TestCurrentUserService) currentUserService).setCurrentUser(secondClientId, false);
        List<StockOrder> secondUserOrders = orderService.listStockOrders();
        assertEquals(1, firstUserOrders.size());
        assertEquals(firstUserOrder.getId(), firstUserOrders.getFirst().getId());
        assertEquals(clientId, firstUserOrders.getFirst().getClientId());

        assertEquals(1, secondUserOrders.size());
        assertEquals(secondUserOrder.getId(), secondUserOrders.getFirst().getId());
        assertEquals(secondClientId, secondUserOrders.getFirst().getClientId());
    }

    @Test
    void listStockOrders_whenManagerCanSeeAll_shouldReturnOrdersOfAllUsers() {
        UUID secondClientId = UUID.randomUUID();

        StockOrder firstUserOrder = orderService.createStockOrder(clientId, carId);
        UUID secondCarId = UUID.randomUUID();
        carRepo.save(createAvailableCar(secondCarId));
        StockOrder secondUserOrder = orderService.createStockOrder(secondClientId, secondCarId);

        ((TestCurrentUserService) currentUserService).setCurrentUser(managerId, true);
        List<StockOrder> result = orderService.listStockOrders();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(order -> order.getId().equals(firstUserOrder.getId())));
        assertTrue(result.stream().anyMatch(order -> order.getId().equals(secondUserOrder.getId())));
    }

    private Car createAvailableCar(UUID id) {
        return new Car(
                id,
                UUID.randomUUID(),
                "Audi",
                "A4",
                BigDecimal.valueOf(3_500_000),
                BodyType.SEDAN,
                FuelType.GASOLINE,
                190,
                BigDecimal.valueOf(2.0),
                TransmissionType.AUTOMATIC,
                DriveType.AWD,
                Color.WHITE,
                CarStatus.AVAILABLE
        );
    }


    private static class TestCurrentUserService extends CurrentUserService {
        private UUID currentUserId;
        private boolean canSeeAllOrders;

        void setCurrentUser(UUID currentUserId, boolean canSeeAllOrders) {
            this.currentUserId = currentUserId;
            this.canSeeAllOrders = canSeeAllOrders;
        }

        @Override
        public UUID getCurrentUserId() {
            return currentUserId;
        }

        @Override
        public boolean hasAnyRole(String... roles) {
            return canSeeAllOrders;
        }
    }
}
