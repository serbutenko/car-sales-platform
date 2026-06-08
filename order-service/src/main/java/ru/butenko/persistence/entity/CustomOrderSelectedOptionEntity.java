package ru.butenko.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.butenko.domain.enums.ComponentType;

import java.util.UUID;

@Entity
@Table(name = "custom_order_selected_options")
@Getter
@Setter
public class CustomOrderSelectedOptionEntity extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "custom_order_id", nullable = false)
    private CustomOrderEntity customOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type", nullable = false)
    private ComponentType componentType;

    @Column(name = "component_option_id", nullable = false)
    private UUID componentOptionId;
}
