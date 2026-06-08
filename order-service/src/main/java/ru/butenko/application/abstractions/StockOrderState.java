package ru.butenko.application.abstractions;

import ru.butenko.domain.enums.StockOrderStatus;

public interface StockOrderState {
    StockOrderStatus status();

    StockOrderState approvedByManager();
    StockOrderState requestPayment();
    StockOrderState pay();
    StockOrderState readyForDelivery();
    StockOrderState complete();
    StockOrderState cancelled();
}
