package ru.butenko.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.butenko.domain.enums.StockOrderStatus;

import java.util.UUID;

@Entity
@Table(name = "stock_orders")
@Getter
@Setter
public class StockOrderEntity extends BaseEntity {
    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "manager_id", nullable = false)
    private UUID managerId;

    @Column(name = "car_id", nullable = false)
    private UUID carId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockOrderStatus status;
}
