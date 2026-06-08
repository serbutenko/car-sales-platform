package ru.butenko.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Автомобиль, доступный на складе для продажи")
public record AvailableCarResponse(
        @Schema(description = "Идентификатор автомобиля")
        UUID id,
        @Schema(description = "Идентификатор модели")
        UUID modelId,
        @Schema(description = "VIN автомобиля")
        String vin,
        @Schema(description = "Статус автомобиля на складе")
        String status
) {
}
