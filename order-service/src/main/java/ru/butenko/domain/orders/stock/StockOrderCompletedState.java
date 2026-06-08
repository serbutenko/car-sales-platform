package ru.butenko.domain.orders.stock;

import ru.butenko.domain.enums.StockOrderStatus;

public class StockOrderCompletedState extends BaseStockOrderState {
    @Override public StockOrderStatus status() { return StockOrderStatus.COMPLETED; }
}
