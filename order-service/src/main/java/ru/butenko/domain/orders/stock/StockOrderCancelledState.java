package ru.butenko.domain.orders.stock;

import ru.butenko.domain.enums.StockOrderStatus;

public class StockOrderCancelledState extends BaseStockOrderState {
    @Override public StockOrderStatus status() { return StockOrderStatus.CANCELLED; }
}
