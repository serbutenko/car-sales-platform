package ru.butenko.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.butenko.domain.assembly.AssemblyOrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "assembly_orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssemblyOrderEntity {
    @Id
    private UUID id;

    @Column(name = "source_order_id", nullable = false)
    private UUID sourceOrderId;

    @Column(name = "source_order_type", nullable = false)
    private String sourceOrderType;

    @Column(name = "car_id")
    private UUID carId;

    @Column(name = "model_id")
    private UUID modelId;

    @ElementCollection
    @CollectionTable(
            name = "assembly_order_required_components",
            joinColumns = @JoinColumn(name = "assembly_order_id")
    )
    @Column(name = "component_id", nullable = false)
    private List<UUID> requiredComponentIds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssemblyOrderStatus status;

    @Column(name = "warehouse_admin_id")
    private UUID warehouseAdminId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(nullable = false)
    private boolean removed;
}
