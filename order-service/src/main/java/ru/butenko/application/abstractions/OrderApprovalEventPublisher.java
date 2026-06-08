package ru.butenko.application.abstractions;

import ru.butenko.application.event.OrderSentForApprovalEvent;

public interface OrderApprovalEventPublisher {
    void publish(OrderSentForApprovalEvent event);
}
