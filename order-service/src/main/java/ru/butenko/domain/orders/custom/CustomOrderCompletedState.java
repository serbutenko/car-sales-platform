package ru.butenko.domain.orders.custom;

import ru.butenko.domain.enums.CustomOrderStatus;

public class CustomOrderCompletedState extends BaseCustomOrderState {
    @Override public CustomOrderStatus status() { return CustomOrderStatus.COMPLETED; }
}
