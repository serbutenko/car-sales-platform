package ru.butenko.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.butenko.domain.enums.*;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Автомобиль из каталога")
public record CarResponse(
        @Schema(description = "Идентификатор автомобиля")
        UUID id,
        @Schema(description = "Идентификатор модели")
        UUID modelId,
        @Schema(description = "Марка автомобиля")
        String brand,
        @Schema(description = "Название модели")
        String modelName,
        @Schema(description = "Цена автомобиля", example = "3200000")
        BigDecimal price,
        @Schema(description = "Тип кузова")
        BodyType bodyType,
        @Schema(description = "Тип топлива")
        FuelType fuelType,
        @Schema(description = "Мощность двигателя в лошадиных силах")
        int enginePowerHp,
        @Schema(description = "Объём двигателя")
        BigDecimal engineVolume,
        @Schema(description = "Коробка передач")
        TransmissionType transmissionType,
        @Schema(description = "Тип привода")
        DriveType driveType,
        @Schema(description = "Цвет")
        Color color,
        @Schema(description = "Статус автомобиля")
        CarStatus status
) {
}
