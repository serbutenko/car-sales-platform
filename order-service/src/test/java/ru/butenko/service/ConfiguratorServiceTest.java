package ru.butenko.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.butenko.application.components.*;
import ru.butenko.application.service.ConfiguratorService;
import ru.butenko.application.service.ConfiguratorValidator;
import ru.butenko.application.service.PriceCalculator;
import ru.butenko.domain.enums.ComponentType;
import ru.butenko.domain.exception.IncompatibleComponentException;
import ru.butenko.domain.model.*;
import ru.butenko.repository.InMemoryCarModelRepository;
import ru.butenko.repository.InMemoryComponentOptionRepository;



import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ConfiguratorServiceTest {
    private InMemoryCarModelRepository modelRepo;
    private InMemoryComponentOptionRepository optionRepo;
    private ConfiguratorService service;

    private UUID modelId;
    private UUID wheelsId;
    private UUID transmissionId;
    private UUID steeringId;
    private UUID interiorOkId;
    private UUID interiorBadId;

    @BeforeEach
    void setUp() {
        modelRepo = new InMemoryCarModelRepository();
        optionRepo = new InMemoryComponentOptionRepository();

        var registry = new DetailFactoryRegistry(List.of(
                new WheelsDetailCreator(optionRepo),
                new TransmissionDetailCreator(optionRepo),
                new SteeringWheelDetailCreator(optionRepo),
                new InteriorDetailCreator(optionRepo)
        ));

        service = new ConfiguratorService(
                modelRepo,
                registry,
                new ConfiguratorValidator(),
                new PriceCalculator()
        );
        modelId = UUID.randomUUID();
        CarModel model = new CarModel(
                modelId,
                "BMW",
                "320i",
                new BigDecimal("3000000"),
                Set.of(
                        ComponentType.WHEELS,
                        ComponentType.TRANSMISSION,
                        ComponentType.STEERING_WHEEL,
                        ComponentType.INTERIOR
                )
        );
        modelRepo.save(model);

        wheelsId = UUID.randomUUID();
        transmissionId = UUID.randomUUID();
        steeringId = UUID.randomUUID();
        interiorOkId = UUID.randomUUID();
        optionRepo.save(new ComponentOption(
                wheelsId,
                ComponentType.WHEELS,
                "19'' M-Sport shya",
                new BigDecimal("95000"),
                Set.of(modelId)
        ));

        optionRepo.save(new ComponentOption(
                transmissionId,
                ComponentType.TRANSMISSION,
                "some kind of transmission",
                BigDecimal.ZERO,
                Set.of(modelId)
        ));

        optionRepo.save(new ComponentOption(
                steeringId,
                ComponentType.STEERING_WHEEL,
                "M-Sport supercool",
                new BigDecimal("25000"),
                Set.of(modelId)
        ));

        optionRepo.save(new ComponentOption(
                interiorOkId,
                ComponentType.INTERIOR,
                "coolest leather interior whuaaaaa",
                new BigDecimal("110000"),
                Set.of(modelId)
        ));

        interiorBadId = UUID.randomUUID();
        optionRepo.save(new ComponentOption(
                interiorBadId,
                ComponentType.INTERIOR,
                "NOT coolest leather interior whuaaaaa",
                new BigDecimal("160000"),
                Set.of(UUID.randomUUID())
        ));
    }

    @Test
    public void build_success_shouldReturnConfigurationWithCorrectTotalPrice() {
        var selected = new EnumMap<ComponentType, UUID>(ComponentType.class);
        selected.put(ComponentType.WHEELS, wheelsId);
        selected.put(ComponentType.TRANSMISSION, transmissionId);
        selected.put(ComponentType.STEERING_WHEEL, steeringId);
        selected.put(ComponentType.INTERIOR, interiorOkId);

        CarConfiguration config = service.build(new ConfigurationRequest(modelId, selected));

        assertEquals(modelId, config.getModelId());
        assertEquals(4, config.getSelectedOptions().size());
        assertEquals(new BigDecimal("3230000"), config.getPrice());
    }

    @Test
    public void build_incompatibleOption_shouldThrowIncompatibleComponentException() {
        var selected = new EnumMap<ComponentType, UUID>(ComponentType.class);
        selected.put(ComponentType.WHEELS, wheelsId);
        selected.put(ComponentType.TRANSMISSION, transmissionId);
        selected.put(ComponentType.STEERING_WHEEL, steeringId);
        selected.put(ComponentType.INTERIOR, interiorBadId);

        assertThrows(IncompatibleComponentException.class,
                () -> service.build(new ConfigurationRequest(modelId, selected)));
    }
}
