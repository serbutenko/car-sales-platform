package ru.butenko.service;

import org.junit.jupiter.api.Test;
import ru.butenko.application.abstractions.StockOrderState;
import ru.butenko.domain.enums.StockOrderStatus;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.orders.stock.StockOrderIssuedState;
import ru.butenko.domain.orders.stock.StockOrderPaidState;
import ru.butenko.domain.orders.stock.StockOrderWaitingPaymentState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StockOrderStateTest {
    @Test
    void happyPath_shouldReachCompleted() {
        StockOrderState state = new StockOrderIssuedState();
        assertEquals(StockOrderStatus.ISSUED, state.status());

        state = state.approvedByManager();
        assertEquals(StockOrderStatus.MANAGER_APPROVED, state.status());

        state = state.requestPayment();
        assertEquals(StockOrderStatus.WAITING_PAYMENT, state.status());

        state = state.pay();
        assertEquals(StockOrderStatus.PAID, state.status());

        state = state.readyForDelivery();
        assertEquals(StockOrderStatus.READY_FOR_DELIVERY, state.status());

        state = state.complete();
        assertEquals(StockOrderStatus.COMPLETED, state.status());
    }

    @Test
    void invalidTransition_fromPaid_toIssued_shouldThrow() {
        StockOrderState state = new StockOrderPaidState();
        assertEquals(StockOrderStatus.PAID, state.status());

        assertThrows(DomainValidationException.class, state::approvedByManager);
    }

    @Test
    void cancelFromAnyNonFinal_shouldGoToCancelled() {
        StockOrderState state = new StockOrderWaitingPaymentState();
        state = state.cancelled();
        assertEquals(StockOrderStatus.CANCELLED, state.status());
    }
}
