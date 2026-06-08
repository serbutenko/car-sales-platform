package ru.butenko.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.butenko.domain.enums.ComponentType;

import java.util.Map;
import java.util.UUID;

@Schema(description = "Запрос на сборку конфигурации автомобиля")
public record ConfigurationRequestDto(
        @Schema(description = "Идентификатор модели автомобиля")
        UUID modelId,
        @Schema(description = "Выбранные комплектующие по типам компонентов")
        Map<ComponentType, UUID> componentIds
) {
}
