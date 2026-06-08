package ru.butenko.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.butenko.domain.enums.ComponentType;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "component_options")
@Getter
@Setter
public class ComponentOptionEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComponentType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal priceDelta;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "component_option_compatible_models",
            joinColumns = @JoinColumn(name = "component_option_id")
    )
    @Column(name = "car_model_id", nullable = false)
    private Set<UUID> compatibleModelIds = new HashSet<>();
}
