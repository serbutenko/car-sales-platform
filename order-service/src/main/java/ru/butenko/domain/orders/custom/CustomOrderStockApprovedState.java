package ru.butenko.domain.orders.custom;

import ru.butenko.application.abstractions.CustomOrderState;
import ru.butenko.domain.enums.CustomOrderStatus;

public class CustomOrderStockApprovedState extends BaseCustomOrderState {
    @Override public CustomOrderStatus status() { return CustomOrderStatus.STOCK_APPROVED; }

    @Override public CustomOrderState requestPayment() { return new CustomOrderWaitingPaymentState(); }

    @Override public CustomOrderState cancelled() { return new CustomOrderCancelledState();}
}
