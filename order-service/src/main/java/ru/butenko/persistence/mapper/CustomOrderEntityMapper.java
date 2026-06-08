package ru.butenko.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.butenko.application.abstractions.Detail;
import ru.butenko.domain.enums.ComponentType;
import ru.butenko.domain.model.CarConfiguration;
import ru.butenko.domain.model.ComponentOption;
import ru.butenko.domain.orders.custom.CustomOrder;
import ru.butenko.persistence.entity.ComponentOptionEntity;
import ru.butenko.persistence.entity.CustomOrderEntity;
import ru.butenko.persistence.entity.CustomOrderSelectedOptionEntity;
import ru.butenko.persistence.repository.SpringDataComponentOptionRepository;

import java.util.EnumMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class CustomOrderEntityMapper {

    private final CustomOrderStateFactory stateFactory;
    private final ComponentOptionEntityMapper componentOptionEntityMapper;
    private final SpringDataComponentOptionRepository componentOptionRepository;

    public CustomOrder toDomain(CustomOrderEntity entity) {
        Map<ComponentType, Detail> selectedOptions = new EnumMap<>(ComponentType.class);

        for (CustomOrderSelectedOptionEntity selected : entity.getSelectedOptions()) {
            ComponentOptionEntity optionEntity = componentOptionRepository.findById(selected.getComponentOptionId())
                    .orElseThrow(() -> new IllegalStateException("Component option not found: " + selected.getComponentOptionId()));

            ComponentOption option = componentOptionEntityMapper.toDomain(optionEntity);
            selectedOptions.put(selected.getComponentType(), option);
        }

        CarConfiguration configuration = new CarConfiguration(
                entity.getModelId(),
                selectedOptions,
                entity.getConfigurationPrice()
        );

        return new CustomOrder(
                entity.getId(),
                entity.getClientId(),
                entity.getManagerId(),
                entity.getModelId(),
                configuration,
                stateFactory.restoreState(entity.getStatus())
        );
    }

    public CustomOrderEntity toEntity(CustomOrder domain) {
        CustomOrderEntity entity = new CustomOrderEntity();
        entity.setId(domain.getId());
        entity.setClientId(domain.getClientId());
        entity.setManagerId(domain.getManagerId());
        entity.setModelId(domain.getModelId());
        entity.setConfigurationPrice(domain.getConfiguration().getPrice());
        entity.setStatus(domain.getStatus());

        for (Map.Entry<ComponentType, Detail> entry : domain.getConfiguration().getSelectedOptions().entrySet()) {
            ComponentOption option = (ComponentOption) entry.getValue();

            CustomOrderSelectedOptionEntity selectedEntity = new CustomOrderSelectedOptionEntity();
            selectedEntity.setCustomOrder(entity);
            selectedEntity.setComponentType(entry.getKey());
            selectedEntity.setComponentOptionId(option.getId());

            entity.getSelectedOptions().add(selectedEntity);
        }

        return entity;
    }
}
