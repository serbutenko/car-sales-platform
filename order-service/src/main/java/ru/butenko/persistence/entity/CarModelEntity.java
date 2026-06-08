package ru.butenko.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.butenko.domain.enums.ComponentType;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "car_models")
@Getter
@Setter
public class CarModelEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "car_model_required_components",
            joinColumns = @JoinColumn(name = "car_model_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "component_type", nullable = false)
    private Set<ComponentType> requiredComponents = new HashSet<>();
}
