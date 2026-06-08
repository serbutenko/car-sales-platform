package ru.butenko.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.butenko.application.service.TestDriveService;
import ru.butenko.domain.enums.BodyType;
import ru.butenko.domain.enums.CarStatus;
import ru.butenko.domain.enums.Color;
import ru.butenko.domain.enums.DriveType;
import ru.butenko.domain.enums.FuelType;
import ru.butenko.domain.enums.TransmissionType;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.Car;
import ru.butenko.repository.InMemoryCarRepository;
import ru.butenko.repository.InMemoryTestDriveCarRepository;
import ru.butenko.repository.InMemoryTestDriveRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TestDriveServiceTest {
    private InMemoryCarRepository carRepo;
    private InMemoryTestDriveRepository testDriveRepo;
    private InMemoryTestDriveCarRepository testDriveCarRepo;
    private TestDriveService testDriveService;

    private UUID carId;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        carRepo = new InMemoryCarRepository();
        testDriveRepo = new InMemoryTestDriveRepository();
        testDriveCarRepo = new InMemoryTestDriveCarRepository();
        testDriveService = new TestDriveService(carRepo, testDriveRepo, testDriveCarRepo);

        carId = UUID.randomUUID();
        clientId = UUID.randomUUID();

        Car car = new Car(
                carId,
                UUID.randomUUID(),
                "BMW",
                "320i",
                BigDecimal.valueOf(3_000_000),
                BodyType.SEDAN,
                FuelType.GASOLINE,
                184,
                BigDecimal.valueOf(2.0),
                TransmissionType.AUTOMATIC,
                DriveType.RWD,
                Color.BLACK,
                CarStatus.AVAILABLE
        );
        carRepo.save(car);
    }

    @Test
    void createRequest_whenCarNotAllowed_shouldThrow() {
        var time = LocalDateTime.now().plusDays(1);
        assertThrows(DomainValidationException.class,
                () -> testDriveService.createRequest(clientId, carId, time));
    }

    @Test
    void createRequest_success() {
        testDriveService.addCarToTestDrive(carId);

        var time = LocalDateTime.now().plusDays(1);
        var req = testDriveService.createRequest(clientId, carId, time);

        assertNotNull(req.getId());
        assertEquals(clientId, req.getClientId());
        assertEquals(carId, req.getCarId());
        assertEquals(time, req.getStartAt());
    }

    @Test
    void createRequest_sameCarSameTime_shouldThrow() {
        testDriveService.addCarToTestDrive(carId);

        var time = LocalDateTime.now().plusDays(1);
        testDriveService.createRequest(clientId, carId, time);

        assertThrows(DomainValidationException.class,
                () -> testDriveService.createRequest(UUID.randomUUID(), carId, time));
    }
}
