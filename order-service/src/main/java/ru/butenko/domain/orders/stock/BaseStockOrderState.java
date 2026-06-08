package ru.butenko.domain.orders.stock;

import ru.butenko.application.abstractions.StockOrderState;
import ru.butenko.domain.exception.DomainValidationException;

public abstract class BaseStockOrderState implements StockOrderState {
    protected DomainValidationException invalid(String action) {
        return new DomainValidationException("Invalid action: " + action);
    }

    @Override public StockOrderState approvedByManager() { throw invalid("approvedByManager"); }
    @Override public StockOrderState requestPayment() { throw invalid("requestPayment"); }
    @Override public StockOrderState pay() { throw invalid("pay"); }
    @Override public StockOrderState readyForDelivery() { throw invalid("readyForDelivery"); }
    @Override public StockOrderState complete() { throw invalid("complete"); }
    @Override public StockOrderState cancelled() { throw invalid("cancelled"); }

}
