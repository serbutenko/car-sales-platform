package ru.butenko.domain.orders.custom;

import ru.butenko.application.abstractions.CustomOrderState;
import ru.butenko.domain.enums.CustomOrderStatus;

public class CustomOrderWaitingPaymentState extends BaseCustomOrderState {
    @Override public CustomOrderStatus status() { return CustomOrderStatus.WAITING_PAYMENT; }

    @Override public CustomOrderState pay() { return new CustomOrderPaidState(); }

    @Override public CustomOrderState cancelled() { return new CustomOrderCancelledState();}
}
