package ru.butenko.application.components;

import ru.butenko.application.abstractions.Detail;
import ru.butenko.application.abstractions.DetailCreator;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.exception.IncompatibleComponentException;
import ru.butenko.domain.model.ComponentOption;
import ru.butenko.application.abstractions.BaseRepository;

import java.util.UUID;

public abstract class BaseDetailCreator implements DetailCreator {
    protected final BaseRepository<UUID, ComponentOption> optionRepo;

    protected BaseDetailCreator(BaseRepository<UUID, ComponentOption> optionRepo) {
        this.optionRepo = optionRepo;
    }

    @Override
    public Detail create(UUID optionId, UUID modelId) {
        if (optionId == null) {
            throw new DomainValidationException("optionId is null");
        }

        if (modelId == null) {
            throw new DomainValidationException("modelId is null");
        }

        ComponentOption option = optionRepo.findById(optionId);

        if (option.getType() != supportedType()) {
            throw new DomainValidationException("option type not supported");
        }

        if (!option.isCompatibleWith(modelId)) {
            throw new IncompatibleComponentException("modelId not compatible");
        }

        return option;
    }
}
