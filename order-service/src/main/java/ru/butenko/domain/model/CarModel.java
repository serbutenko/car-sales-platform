package ru.butenko.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.butenko.domain.enums.ComponentType;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CarModel {

    private final UUID id;
    private final String name;
    private final String brand;
    private final BigDecimal price;
    private final Set<ComponentType> requiredComponents;
}
