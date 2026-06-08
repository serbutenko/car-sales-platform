package ru.butenko.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.butenko.domain.enums.ComponentType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Schema(description = "Собранная конфигурация автомобиля")
public record CarConfigurationResponse(
        @Schema(description = "Идентификатор модели автомобиля")
        UUID modelId,
        @Schema(description = "Выбранные комплектующие по типам компонентов")
        Map<ComponentType, ComponentOptionResponse> selectedOptions,
        @Schema(description = "Итоговая стоимость конфигурации", example = "3550000")
        BigDecimal price
) {
}
