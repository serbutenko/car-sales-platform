package ru.butenko.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.butenko.application.abstractions.CarRepository;
import ru.butenko.application.abstractions.TestDriveCarRepository;
import ru.butenko.application.abstractions.TestDriveRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.Car;
import ru.butenko.domain.model.TestDriveRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestDriveService {
    private final CarRepository carRepo;
    private final TestDriveRepository testDriveRepo;
    private final TestDriveCarRepository testDriveCarRepo;

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public void addCarToTestDrive(UUID carId) {
        if (carId == null) throw new DomainValidationException("carId is null");
        ensureCarExists(carId);
        testDriveCarRepo.add(carId);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public void removeCarFromTestDrive(UUID carId) {
        if (carId == null) throw new DomainValidationException("carId is null");
        testDriveCarRepo.remove(carId);
    }

    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public Set<UUID> listTestDriveCars() {
        return testDriveCarRepo.findAllCarIds().stream().collect(Collectors.toSet());
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public TestDriveRequest createRequest(UUID clientId, UUID carId, LocalDateTime startAt) {
        if (clientId == null) throw new DomainValidationException("clientId is null");
        if (carId == null) throw new DomainValidationException("carId is null");
        if (startAt == null) throw new DomainValidationException("startAt is null");

        ensureCarExists(carId);
        if (!testDriveCarRepo.exists(carId)) {
            throw new DomainValidationException("carId is not allowed for test drive: " + carId);
        }

        boolean busy = testDriveRepo.existsByCarIdAndStartAt(carId, startAt);

        if (busy) {
            throw new DomainValidationException("This time slot is already taken for car: " + carId);
        }

        TestDriveRequest req = new TestDriveRequest(UUID.randomUUID(), clientId, carId, startAt);
        return testDriveRepo.save(req);
    }


    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public List<TestDriveRequest> listRequests(int page, int size) {
        return testDriveRepo.findAll(page, size);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public TestDriveRequest getRequest(UUID requestId) {
        if (requestId == null) throw new DomainValidationException("requestId is null");
        return testDriveRepo.findById(requestId);
    }

    private Car ensureCarExists(UUID carId) {
        return carRepo.findById(carId);
    }
}
