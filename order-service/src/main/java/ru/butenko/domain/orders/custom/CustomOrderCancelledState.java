package ru.butenko.domain.orders.custom;

import ru.butenko.domain.enums.CustomOrderStatus;

public class CustomOrderCancelledState extends BaseCustomOrderState {
    @Override public CustomOrderStatus status() { return CustomOrderStatus.CANCELLED; }
}
