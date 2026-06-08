package ru.butenko.domain.orders.custom;

import ru.butenko.application.abstractions.CustomOrderState;
import ru.butenko.domain.enums.CustomOrderStatus;

public class CustomOrderWaitForDeliveryState extends BaseCustomOrderState {
    @Override public CustomOrderStatus status() { return CustomOrderStatus.WAITING_FOR_DELIVERY; }

    @Override public CustomOrderState readyForDelivery() { return new CustomOrderReadyForDeliveryState(); }
}
