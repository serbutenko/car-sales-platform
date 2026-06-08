package ru.butenko.domain.model;

import lombok.Getter;
import lombok.Setter;
import ru.butenko.domain.enums.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CarFilter {
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private String brand;
    private String modelName;
    private BigDecimal minEnginePowerHp;
    private BigDecimal maxEnginePowerHp;
    private BigDecimal minEngineVolume;
    private BigDecimal maxEngineVolume;
    private TransmissionType transmissionType;
    private DriveType driveType;
    private BodyType bodyType;
    private FuelType fuelType;
    private Color color;
    private List<UUID> componentOptionIds;
}
