package ru.butenko.application.abstractions;

import ru.butenko.domain.enums.ComponentType;

import java.util.UUID;

public interface DetailCreator {
    ComponentType supportedType();
    Detail create(UUID optionId, UUID modelId);
}
