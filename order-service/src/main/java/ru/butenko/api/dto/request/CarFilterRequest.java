package ru.butenko.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.butenko.domain.enums.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Schema(description = "Параметры фильтрации каталога автомобилей")
public record CarFilterRequest(
        @Schema(description = "Минимальная цена автомобиля", example = "2500000")
        BigDecimal minPrice,
        @Schema(description = "Максимальная цена автомобиля", example = "4500000")
        BigDecimal maxPrice,
        @Schema(description = "Марка автомобиля", example = "BMW")
        String brand,
        @Schema(description = "Название модели", example = "320i")
        String modelName,
        @Schema(description = "Минимальная мощность двигателя в лошадиных силах", example = "150")
        BigDecimal minEnginePowerHp,
        @Schema(description = "Максимальная мощность двигателя в лошадиных силах", example = "300")
        BigDecimal maxEnginePowerHp,
        @Schema(description = "Минимальный объём двигателя", example = "2.0")
        BigDecimal minEngineVolume,
        @Schema(description = "Максимальный объём двигателя", example = "3.0")
        BigDecimal maxEngineVolume,
        @Schema(description = "Тип коробки передач")
        TransmissionType transmissionType,
        @Schema(description = "Тип привода")
        DriveType driveType,
        @Schema(description = "Тип кузова")
        BodyType bodyType,
        @Schema(description = "Тип топлива")
        FuelType fuelType,
        @Schema(description = "Цвет автомобиля")
        Color color,
        @Schema(description = "Список идентификаторов комплектующих, совместимых с моделью")
        List<UUID> componentOptionIds
) {
}
