package ru.butenko.domain.orders.stock;

import ru.butenko.application.abstractions.StockOrderState;
import ru.butenko.domain.enums.StockOrderStatus;

public class StockOrderPaidState extends BaseStockOrderState {
    @Override public StockOrderStatus status() { return StockOrderStatus.PAID; }

    @Override public StockOrderState readyForDelivery() { return new StockOrderReadyForDeliveryState(); }
    @Override public StockOrderState cancelled() { return new StockOrderCancelledState(); }
}
