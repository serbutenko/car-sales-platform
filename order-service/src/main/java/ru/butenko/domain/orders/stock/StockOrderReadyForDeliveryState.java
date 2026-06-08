package ru.butenko.domain.orders.stock;

import ru.butenko.application.abstractions.StockOrderState;
import ru.butenko.domain.enums.StockOrderStatus;

public class StockOrderReadyForDeliveryState extends BaseStockOrderState {
    @Override public StockOrderStatus status() { return StockOrderStatus.READY_FOR_DELIVERY; }

    @Override public StockOrderState complete() { return new StockOrderCompletedState(); }
}
