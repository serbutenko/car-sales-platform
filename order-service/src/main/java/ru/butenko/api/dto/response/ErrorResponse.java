package ru.butenko.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Стандартный формат ошибки API")
public record ErrorResponse(
        @Schema(description = "Время возникновения ошибки")
        Instant timestamp,
        @Schema(description = "HTTP-статус", example = "400")
        int status,
        @Schema(description = "Короткое название ошибки", example = "Bad Request")
        String error,
        @Schema(description = "Подробное сообщение ошибки")
        String message,
        @Schema(description = "Путь запроса, на котором произошла ошибка")
        String path
) {
}
