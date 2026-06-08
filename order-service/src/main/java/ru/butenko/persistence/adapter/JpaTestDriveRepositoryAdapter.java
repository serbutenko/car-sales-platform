package ru.butenko.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.butenko.application.abstractions.TestDriveRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.TestDriveRequest;
import ru.butenko.persistence.mapper.TestDriveRequestEntityMapper;
import ru.butenko.persistence.repository.SpringDataCarRepository;
import ru.butenko.persistence.repository.SpringDataTestDriveRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class JpaTestDriveRepositoryAdapter implements TestDriveRepository {

    private final SpringDataTestDriveRequestRepository repository;
    private final SpringDataCarRepository carRepository;
    private final TestDriveRequestEntityMapper mapper;

    @Override
    public TestDriveRequest save(TestDriveRequest entity) {
        var carEntity = carRepository.findById(entity.getCarId())
                .orElseThrow(() -> new DomainValidationException("Car not found: " + entity.getCarId()));

        return mapper.toDomain(repository.save(mapper.toEntity(entity, carEntity)));
    }

    @Override
    public TestDriveRequest findById(UUID id) {
        return repository.findByIdAndRemovedFalse(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new DomainValidationException("Entity not found: " + id));
    }

    @Override
    public List<TestDriveRequest> findAll() {
        return repository.findAllByRemovedFalse().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new DomainValidationException("Entity not found: " + id));
        entity.setRemoved(true);
        repository.save(entity);
    }

    @Override
    public List<TestDriveRequest> findAll(int page, int size) {
        return repository.findAllByRemovedFalse(
                PageRequest.of(page, size, Sort.by("startAt"))
        ).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByCarIdAndStartAt(UUID carId, LocalDateTime startAt) {
        return repository.existsByCar_IdAndStartAtAndRemovedFalse(carId, startAt);
    }
}
