package ru.butenko.domain.orders.stock;

import ru.butenko.application.abstractions.StockOrderState;
import ru.butenko.domain.enums.StockOrderStatus;

public class StockOrderManagerApprovedState extends BaseStockOrderState {
    @Override public StockOrderStatus status() { return StockOrderStatus.MANAGER_APPROVED; }

    @Override public StockOrderState requestPayment() { return new StockOrderWaitingPaymentState(); }

    @Override public StockOrderState cancelled() { return new StockOrderCancelledState();}
}
