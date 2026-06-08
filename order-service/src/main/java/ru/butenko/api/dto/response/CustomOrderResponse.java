package ru.butenko.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.butenko.domain.enums.CustomOrderStatus;

import java.util.UUID;

@Schema(description = "Ответ по кастомному заказу")
public record CustomOrderResponse(
        @Schema(description = "Идентификатор заказа")
        UUID id,
        @Schema(description = "Идентификатор клиента")
        UUID clientId,
        @Schema(description = "Идентификатор менеджера")
        UUID managerId,
        @Schema(description = "Идентификатор модели автомобиля")
        UUID modelId,
        @Schema(description = "Собранная конфигурация заказа")
        CarConfigurationResponse configuration,
        @Schema(description = "Текущий статус заказа")
        CustomOrderStatus status
) {
}
