package ru.butenko.api.mapper;

import org.springframework.stereotype.Component;
import ru.butenko.api.dto.request.ConfigurationRequestDto;
import ru.butenko.api.dto.response.CarConfigurationResponse;
import ru.butenko.api.dto.response.ComponentOptionResponse;
import ru.butenko.application.abstractions.Detail;
import ru.butenko.domain.enums.ComponentType;
import ru.butenko.domain.model.CarConfiguration;
import ru.butenko.domain.model.ComponentOption;
import ru.butenko.domain.model.ConfigurationRequest;

import java.util.EnumMap;
import java.util.Map;

@Component
public class ConfiguratorDtoMapper {

    public ConfigurationRequest toDomain(ConfigurationRequestDto dto) {
        return new ConfigurationRequest(
                dto.modelId(),
                dto.componentIds()
        );
    }

    public CarConfigurationResponse toResponse(CarConfiguration configuration) {
        Map<ComponentType, ComponentOptionResponse> selectedOptions = new EnumMap<>(ComponentType.class);

        for (Map.Entry<ComponentType, Detail> entry : configuration.getSelectedOptions().entrySet()) {
            ComponentOption option = (ComponentOption) entry.getValue();

            selectedOptions.put(
                    entry.getKey(),
                    new ComponentOptionResponse(
                            option.getId(),
                            option.getType(),
                            option.getName(),
                            option.getPriceDelta()
                    )
            );
        }

        return new CarConfigurationResponse(
                configuration.getModelId(),
                selectedOptions,
                configuration.getPrice()
        );
    }
}
