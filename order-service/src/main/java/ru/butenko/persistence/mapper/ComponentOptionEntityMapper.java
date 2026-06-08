package ru.butenko.persistence.mapper;

import org.springframework.stereotype.Component;
import ru.butenko.domain.model.ComponentOption;
import ru.butenko.persistence.entity.ComponentOptionEntity;

@Component
public class ComponentOptionEntityMapper {

    public ComponentOption toDomain(ComponentOptionEntity entity) {
        return new ComponentOption(
                entity.getId(),
                entity.getType(),
                entity.getName(),
                entity.getPriceDelta(),
                entity.getCompatibleModelIds()
        );
    }

    public ComponentOptionEntity toEntity(ComponentOption domain) {
        ComponentOptionEntity entity = new ComponentOptionEntity();
        entity.setId(domain.getId());
        entity.setType(domain.getType());
        entity.setName(domain.getName());
        entity.setPriceDelta(domain.getPriceDelta());
        entity.setCompatibleModelIds(domain.getCompatibleModelIds());
        return entity;
    }
}
