package ru.butenko.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.butenko.application.abstractions.Detail;
import ru.butenko.domain.enums.ComponentType;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ComponentOption implements Detail {

    private final UUID  id;
    private final ComponentType type;
    private final String name;
    private final BigDecimal priceDelta;
    private final Set<UUID> compatibleModelIds;

    public boolean isCompatibleWith(UUID modelId) {
        return compatibleModelIds.contains(modelId);
    }
}
