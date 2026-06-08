package ru.butenko.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.butenko.domain.enums.ComponentType;

import java.util.Map;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ConfigurationRequest {
    private final UUID modelId;
    private final Map<ComponentType, UUID> componentIds;
}
