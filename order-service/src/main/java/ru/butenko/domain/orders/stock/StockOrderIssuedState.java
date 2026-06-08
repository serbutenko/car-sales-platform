package ru.butenko.domain.orders.stock;

import ru.butenko.application.abstractions.StockOrderState;
import ru.butenko.domain.enums.StockOrderStatus;

public class StockOrderIssuedState extends BaseStockOrderState {
    @Override public StockOrderStatus status() { return StockOrderStatus.ISSUED; }

    @Override public StockOrderState approvedByManager() { return new StockOrderManagerApprovedState(); }

    @Override public StockOrderState cancelled() { return new StockOrderCancelledState();}
}


