package ru.butenko.persistence.mapper;

import org.springframework.stereotype.Component;
import ru.butenko.application.abstractions.StockOrderState;
import ru.butenko.domain.enums.StockOrderStatus;
import ru.butenko.domain.orders.stock.*;

import static ru.butenko.domain.enums.StockOrderStatus.ISSUED;
import static ru.butenko.domain.enums.StockOrderStatus.MANAGER_APPROVED;

@Component
public class StockOrderStateFactory {
    public StockOrderState restoreState(StockOrderStatus status) {
        return switch (status) {
            case ISSUED -> new StockOrderIssuedState();
            case MANAGER_APPROVED -> new StockOrderManagerApprovedState();
            case WAITING_PAYMENT -> new StockOrderWaitingPaymentState();
            case PAID -> new StockOrderPaidState();
            case READY_FOR_DELIVERY -> new StockOrderReadyForDeliveryState();
            case COMPLETED -> new StockOrderCompletedState();
            case CANCELLED -> new StockOrderCancelledState();
        };
    }
}
