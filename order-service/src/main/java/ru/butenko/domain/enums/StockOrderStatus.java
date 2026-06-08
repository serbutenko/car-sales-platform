package ru.butenko.domain.enums;

public enum StockOrderStatus {
    ISSUED,
    MANAGER_APPROVED,
    WAITING_PAYMENT,
    PAID,
    READY_FOR_DELIVERY,
    COMPLETED,
    CANCELLED
}
