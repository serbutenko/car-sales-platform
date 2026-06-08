package ru.butenko.application.components;

import ru.butenko.domain.model.ComponentOption;
import ru.butenko.domain.enums.ComponentType;
import ru.butenko.application.abstractions.BaseRepository;

import java.util.UUID;

public class InteriorDetailCreator extends BaseDetailCreator {
    public InteriorDetailCreator(BaseRepository<UUID, ComponentOption> repository) {
        super(repository);
    }

    @Override
    public ComponentType supportedType() {
        return ComponentType.INTERIOR;
    }
}
