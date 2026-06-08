package ru.butenko.domain.orders.stock;

import ru.butenko.application.abstractions.StockOrderState;
import ru.butenko.domain.enums.StockOrderStatus;

public class StockOrderWaitingPaymentState extends BaseStockOrderState {
    @Override public StockOrderStatus status() { return StockOrderStatus.WAITING_PAYMENT; }

    @Override public StockOrderState pay() { return new StockOrderPaidState(); }

    @Override public StockOrderState cancelled() { return new StockOrderCancelledState();}
}
