package ru.butenko.domain.orders.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.butenko.application.abstractions.CustomOrderState;
import ru.butenko.domain.enums.CustomOrderStatus;
import ru.butenko.domain.model.CarConfiguration;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomOrder {
    private final UUID id;
    private final UUID clientId;
    private final UUID managerId;
    private final UUID modelId;
    private final CarConfiguration configuration;
    private CustomOrderState state;

    public CustomOrder(UUID id, UUID clientId, UUID managerId, UUID modelId, CarConfiguration configuration) {
        this(id, clientId, managerId, modelId, configuration, new CustomOrderIssuedState());
    }

    public CustomOrderStatus getStatus() {
        return state.status();
    }

    public void approveByStock() {
        state = state.approvedByStock();
    }

    public void requestPayment() {
        state = state.requestPayment();
    }

    public void pay() {
        state = state.pay();
    }

    public void waitForDelivery() {
        state = state.waitForDelivery();
    }

    public void readyForDelivery() {
        state = state.readyForDelivery();
    }

    public void complete() {
        state = state.complete();
    }

    public void cancel() {
        state = state.cancelled();
    }
}
