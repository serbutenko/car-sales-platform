package ru.butenko.service;

import org.junit.jupiter.api.Test;
import ru.butenko.application.abstractions.CustomOrderState;
import ru.butenko.domain.enums.CustomOrderStatus;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.orders.custom.CustomOrderIssuedState;
import ru.butenko.domain.orders.custom.CustomOrderPaidState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomOrderStateTest {
    @Test
    void happyPath_shouldReachCompleted() {
        CustomOrderState state = new CustomOrderIssuedState();
        assertEquals(CustomOrderStatus.ISSUED, state.status());

        state = state.approvedByStock();
        assertEquals(CustomOrderStatus.STOCK_APPROVED, state.status());

        state = state.requestPayment();
        assertEquals(CustomOrderStatus.WAITING_PAYMENT, state.status());

        state = state.pay();
        assertEquals(CustomOrderStatus.PAID, state.status());

        state = state.waitForDelivery();
        assertEquals(CustomOrderStatus.WAITING_FOR_DELIVERY, state.status());

        state = state.readyForDelivery();
        assertEquals(CustomOrderStatus.READY_FOR_DELIVERY, state.status());

        state = state.complete();
        assertEquals(CustomOrderStatus.COMPLETED, state.status());
    }

    @Test
    void invalidTransition_shouldThrow() {
        CustomOrderState state = new CustomOrderPaidState();
        assertEquals(CustomOrderStatus.PAID, state.status());

        assertThrows(DomainValidationException.class, state::approvedByStock);
    }

}
