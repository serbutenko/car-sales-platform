package ru.butenko.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.butenko.application.abstractions.Detail;
import ru.butenko.domain.enums.ComponentType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CarConfiguration {
    private final UUID modelId;
    private final Map<ComponentType, Detail> selectedOptions;
    private final BigDecimal price;
}
