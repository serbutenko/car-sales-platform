package ru.butenko.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Запрос на создание кастомного заказа")
public record CreateCustomOrderRequest(
        @Schema(description = "Параметры конфигурации автомобиля")
        ConfigurationRequestDto configuration
) {
}
