package ru.butenko.domain.orders.custom;

import ru.butenko.application.abstractions.CustomOrderState;
import ru.butenko.domain.enums.CustomOrderStatus;

public class CustomOrderPaidState extends BaseCustomOrderState {
    @Override public CustomOrderStatus status() { return CustomOrderStatus.PAID; }

    @Override public CustomOrderState waitForDelivery() { return new CustomOrderWaitForDeliveryState(); }
    @Override public CustomOrderState cancelled() { return new CustomOrderCancelledState(); }
}
