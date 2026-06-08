package ru.butenko.integration.kafka;

public interface OrderApprovalResultPublisher {
    void publish(OrderApprovalResultEvent event);
}
