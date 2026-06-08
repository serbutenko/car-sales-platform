package ru.butenko.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.butenko.domain.enums.CustomOrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "custom_orders")
@Getter
@Setter
public class CustomOrderEntity extends BaseEntity {

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "manager_id", nullable = false)
    private UUID managerId;

    @Column(name = "model_id", nullable = false)
    private UUID modelId;

    @Column(name = "configuration_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal configurationPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomOrderStatus status;

    @OneToMany(mappedBy = "customOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomOrderSelectedOptionEntity> selectedOptions = new ArrayList<>();
}
