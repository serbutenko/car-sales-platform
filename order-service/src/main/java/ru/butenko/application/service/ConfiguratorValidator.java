package ru.butenko.application.service;

import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.exception.IncompatibleComponentException;
import ru.butenko.domain.model.CarModel;
import ru.butenko.domain.model.ComponentOption;
import ru.butenko.domain.enums.ComponentType;
import ru.butenko.domain.model.ConfigurationRequest;

import java.util.Map;
import java.util.UUID;

public class ConfiguratorValidator {
    public void validateRequest(ConfigurationRequest request) {
        if (request == null) {
            throw new DomainValidationException("Request is null");
        }

        if (request.getModelId() == null) {
            throw new DomainValidationException("ModelId is null");
        }

        if (request.getComponentIds() == null) {
            throw new DomainValidationException("ComponentIds is null");
        }
    }

    public void validateRequiredComponents(
            CarModel model,
            Map<ComponentType, UUID> selectedIDs
    ) {
        if (model.getRequiredComponents() == null ||  model.getRequiredComponents().isEmpty()) {
            throw new DomainValidationException("Required components is null or empty");
        }

        for (ComponentType required : model.getRequiredComponents()) {
            if (!selectedIDs.containsKey(required)) {
                throw new DomainValidationException("Required component " + required + " not found");
            }
        }
    }

    public void validateOptionType(ComponentType required, ComponentOption option) {
        if (option.getType() == null) {
            throw new DomainValidationException("Option type is null");
        }
    }

    public void validateCompatibility(UUID modelId, ComponentOption option) {
        if (!option.isCompatibleWith(modelId)) {
            throw new IncompatibleComponentException("Incompatible component " + modelId + " with option " + option);
        }
    }
}
