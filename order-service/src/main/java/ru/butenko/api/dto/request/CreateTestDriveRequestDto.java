package ru.butenko.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Запрос на создание заявки на тест-драйв")
public record CreateTestDriveRequestDto(
        @Schema(description = "Идентификатор клиента")
        UUID clientId,
        @Schema(description = "Идентификатор автомобиля")
        UUID carId,
        @Schema(description = "Дата и время начала тест-драйва", example = "2026-03-25T10:00:00")
        LocalDateTime startAt
) {
}
