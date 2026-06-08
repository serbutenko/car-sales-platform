package ru.butenko.application.components;

import ru.butenko.application.abstractions.DetailCreator;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.enums.ComponentType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class DetailFactoryRegistry {
    private final Map<ComponentType, DetailCreator> creators = new EnumMap<>(ComponentType.class);

    public DetailFactoryRegistry(List<DetailCreator> creators) {
        for (DetailCreator creator : creators) {
            this.creators.put(creator.supportedType(), creator);
        }
    }

    public DetailCreator getCreator(ComponentType type) {
        DetailCreator creator = creators.get(type);
        if (creator == null) {
            throw new DomainValidationException(String.format("No such DetailCreator: %s", type));
        }
        return creator;
    }
}
