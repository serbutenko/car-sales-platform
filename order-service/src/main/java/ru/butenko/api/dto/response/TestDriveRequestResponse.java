package ru.butenko.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Заявка на тест-драйв")
public record TestDriveRequestResponse(
        @Schema(description = "Идентификатор заявки")
        UUID id,
        @Schema(description = "Идентификатор клиента")
        UUID clientId,
        @Schema(description = "Идентификатор автомобиля")
        UUID carId,
        @Schema(description = "Дата и время начала тест-драйва", example = "2026-03-25T10:00:00")
        LocalDateTime startAt
) {
}
