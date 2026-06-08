package ru.butenko.domain.orders.custom;

import ru.butenko.application.abstractions.CustomOrderState;
import ru.butenko.domain.exception.DomainValidationException;

public abstract class BaseCustomOrderState implements CustomOrderState {
    protected DomainValidationException invalid(String action) {
        return new DomainValidationException("Invalid action: " + action);
    }

    @Override public CustomOrderState approvedByStock() { throw invalid("approvedByStock"); }
    @Override public CustomOrderState requestPayment() { throw invalid("requestPayment"); }
    @Override public CustomOrderState pay() { throw invalid("pay"); }
    @Override public CustomOrderState waitForDelivery() { throw invalid("waitForDelivery"); }
    @Override public CustomOrderState readyForDelivery() { throw invalid("readyForDelivery"); }
    @Override public CustomOrderState complete() { throw invalid("complete"); }
    @Override public CustomOrderState cancelled() { throw invalid("cancelled"); }
}
