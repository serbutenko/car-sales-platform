package ru.butenko.domain.orders.custom;

import ru.butenko.application.abstractions.CustomOrderState;
import ru.butenko.domain.enums.CustomOrderStatus;

public class CustomOrderIssuedState extends BaseCustomOrderState {
    @Override public CustomOrderStatus status() { return CustomOrderStatus.ISSUED; }

    @Override public CustomOrderState approvedByStock() { return new CustomOrderStockApprovedState(); }

    @Override public CustomOrderState cancelled() { return new CustomOrderCancelledState();}
}
