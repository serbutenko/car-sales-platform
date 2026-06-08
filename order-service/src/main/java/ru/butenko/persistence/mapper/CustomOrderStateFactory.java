package ru.butenko.persistence.mapper;

import org.springframework.stereotype.Component;
import ru.butenko.application.abstractions.CustomOrderState;
import ru.butenko.domain.enums.CustomOrderStatus;
import ru.butenko.domain.orders.custom.*;

@Component
public class CustomOrderStateFactory {

    public CustomOrderState restoreState(CustomOrderStatus status) {
        return switch (status) {
            case ISSUED -> new CustomOrderIssuedState();
            case STOCK_APPROVED -> new CustomOrderStockApprovedState();
            case WAITING_PAYMENT -> new CustomOrderWaitingPaymentState();
            case PAID -> new CustomOrderPaidState();
            case WAITING_FOR_DELIVERY -> new CustomOrderWaitForDeliveryState();
            case READY_FOR_DELIVERY -> new CustomOrderReadyForDeliveryState();
            case COMPLETED -> new CustomOrderCompletedState();
            case CANCELLED -> new CustomOrderCancelledState();
        };
    }
}
