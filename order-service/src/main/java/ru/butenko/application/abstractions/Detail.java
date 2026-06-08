package ru.butenko.application.abstractions;

import ru.butenko.domain.enums.ComponentType;

import java.math.BigDecimal;
import java.util.UUID;

public interface Detail {
    UUID getId();
    ComponentType getType();
    boolean isCompatibleWith(UUID modelId);
    BigDecimal getPriceDelta();
}
