package ru.butenko.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.butenko.application.abstractions.CustomOrderRepository;
import ru.butenko.application.abstractions.StockOrderRepository;
import ru.butenko.domain.model.CarConfiguration;
import ru.butenko.domain.orders.custom.CustomOrder;
import ru.butenko.domain.orders.custom.CustomOrderIssuedState;
import ru.butenko.domain.orders.stock.StockOrder;
import ru.butenko.domain.orders.stock.StockOrderIssuedState;
import ru.butenko.security.CurrentUserService;
import ru.butenko.security.OrderSecurityService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderSecurityServiceTest {
    @Mock
    private StockOrderRepository stockOrderRepository;

    @Mock
    private CustomOrderRepository customOrderRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private OrderSecurityService orderSecurityService;

    @Test
    void isStockOrderOwner_shouldReturnTrue_whenCurrentUserOwnsOrder() {
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(stockOrderRepository.findById(orderId)).thenReturn(
                new StockOrder(orderId, userId, UUID.randomUUID(), UUID.randomUUID(), new StockOrderIssuedState())
        );

        assertTrue(orderSecurityService.isStockOrderOwner(orderId));
    }

    @Test
    void isCustomOrderOwner_shouldReturnTrue_whenCurrentUserOwnsOrder() {
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(customOrderRepository.findById(orderId)).thenReturn(
                new CustomOrder(
                        orderId,
                        userId,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new CarConfiguration(UUID.randomUUID(), Map.of(), BigDecimal.TEN),
                        new CustomOrderIssuedState()
                )
        );

        assertTrue(orderSecurityService.isCustomOrderOwner(orderId));
    }

    @Test
    void isStockOrderOwner_shouldReturnFalse_whenCurrentUserDoesNotOwnOrder() {
        UUID userId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(stockOrderRepository.findById(orderId)).thenReturn(
                new StockOrder(orderId, ownerId, UUID.randomUUID(), UUID.randomUUID(), new StockOrderIssuedState())
        );

        assertFalse(orderSecurityService.isStockOrderOwner(orderId));
    }
}
