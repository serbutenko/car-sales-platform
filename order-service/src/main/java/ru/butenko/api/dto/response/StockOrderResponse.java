package ru.butenko.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.butenko.domain.enums.StockOrderStatus;

import java.util.UUID;

@Schema(description = "Ответ по заказу автомобиля со склада")
public record StockOrderResponse(
        @Schema(description = "Идентификатор заказа")
        UUID id,
        @Schema(description = "Идентификатор клиента")
        UUID clientId,
        @Schema(description = "Идентификатор менеджера")
        UUID managerId,
        @Schema(description = "Идентификатор автомобиля")
        UUID carId,
        @Schema(description = "Текущий статус заказа")
        StockOrderStatus status
) {
}
