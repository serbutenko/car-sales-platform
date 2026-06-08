package ru.butenko.domain.orders.custom;

import ru.butenko.application.abstractions.CustomOrderState;
import ru.butenko.domain.enums.CustomOrderStatus;

public class CustomOrderReadyForDeliveryState extends BaseCustomOrderState {
    @Override public CustomOrderStatus status() { return CustomOrderStatus.READY_FOR_DELIVERY; }

    @Override public CustomOrderState complete() { return new CustomOrderCompletedState(); }
}
