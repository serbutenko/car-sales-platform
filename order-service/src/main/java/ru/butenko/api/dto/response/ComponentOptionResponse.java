package ru.butenko.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.butenko.domain.enums.ComponentType;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Комплектующая опция конфигуратора")
public record ComponentOptionResponse(
        @Schema(description = "Идентификатор опции")
        UUID id,
        @Schema(description = "Тип компонента")
        ComponentType type,
        @Schema(description = "Название опции")
        String name,
        @Schema(description = "Изменение цены относительно базовой модели", example = "50000")
        BigDecimal priceDelta
) {
}
