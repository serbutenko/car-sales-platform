package ru.butenko.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Запрос на создание заказа со склада")
public record CreateStockOrderRequest(
        @Schema(description = "Идентификатор автомобиля из наличия")
        UUID carId
) {
}
