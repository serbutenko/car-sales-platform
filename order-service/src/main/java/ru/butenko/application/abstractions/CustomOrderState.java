package ru.butenko.application.abstractions;

import ru.butenko.domain.enums.CustomOrderStatus;

public interface CustomOrderState {
    CustomOrderStatus status();

    CustomOrderState approvedByStock();
    CustomOrderState requestPayment();
    CustomOrderState pay();
    CustomOrderState waitForDelivery();
    CustomOrderState readyForDelivery();
    CustomOrderState complete();
    CustomOrderState cancelled();
}
