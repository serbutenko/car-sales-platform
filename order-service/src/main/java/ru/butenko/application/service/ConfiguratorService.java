package ru.butenko.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.butenko.application.abstractions.Detail;
import ru.butenko.application.components.DetailFactoryRegistry;
import ru.butenko.domain.enums.ComponentType;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.*;
import ru.butenko.application.abstractions.BaseRepository;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfiguratorService {
    private final BaseRepository<UUID, CarModel> carModelRepo;
    private final DetailFactoryRegistry registry;

    private final ConfiguratorValidator validator;
    private final PriceCalculator priceCalculator;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public CarConfiguration build(ConfigurationRequest request) {
        validator.validateRequest(request);
        UUID modelId = request.getModelId();
        CarModel model = carModelRepo.findById(modelId);
        Map<ComponentType, UUID> selectedIds = request.getComponentIds();
        validator.validateRequiredComponents(model, selectedIds);
        Map<ComponentType, Detail> selectedDetails = new EnumMap<>(ComponentType.class);

        for (ComponentType required : model.getRequiredComponents()) {
            UUID optionId = selectedIds.get(required);
            Detail detail = registry.getCreator(required).create(optionId, modelId);

            selectedDetails.put(required, detail);
        }

        BigDecimal total = priceCalculator.calculate(model, selectedDetails.values());
        return new CarConfiguration(modelId, selectedDetails, total);
    }
}
