package ru.butenko.domain.orders.stock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.butenko.application.abstractions.StockOrderState;
import ru.butenko.domain.enums.StockOrderStatus;

import java.util.UUID;

@Getter
public class StockOrder {
    private final UUID id;
    private final UUID clientId;
    private final UUID managerId;
    private final UUID carId;
    private StockOrderState state = new StockOrderIssuedState();

    public StockOrder(UUID id, UUID clientId, UUID managerId, UUID carId) {
        this(id, clientId, managerId, carId, new StockOrderIssuedState());
    }

    public StockOrder(UUID id, UUID clientId, UUID managerId, UUID carId, StockOrderState state) {
        this.id = id;
        this.clientId = clientId;
        this.managerId = managerId;
        this.carId = carId;
        this.state = state;
    }

    public StockOrderStatus getStatus() {
        return state.status();
    }

    public void approveByManager() {
        state = state.approvedByManager();
    }

    public void requestPayment() {
        state = state.requestPayment();
    }

    public void pay() {
        state = state.pay();
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
