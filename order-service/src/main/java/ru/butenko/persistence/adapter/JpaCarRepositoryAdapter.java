package ru.butenko.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.butenko.application.abstractions.CarRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.Car;
import ru.butenko.domain.model.CarFilter;
import ru.butenko.persistence.mapper.CarEntityMapper;
import ru.butenko.persistence.repository.SpringDataCarModelRepository;
import ru.butenko.persistence.repository.SpringDataCarRepository;
import ru.butenko.persistence.specification.CarSpecifications;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class JpaCarRepositoryAdapter implements CarRepository {
    private final SpringDataCarRepository repository;
    private final SpringDataCarModelRepository carModelRepository;
    private final CarEntityMapper mapper;


    @Override
    public Car save(Car entity) {
        var modelEntity = carModelRepository.findById(entity.getModelId())
                .orElseThrow(() -> new DomainValidationException("Car model not found: " + entity.getModelId()));

        return mapper.toDomain(repository.save(mapper.toEntity(entity, modelEntity)));
    }

    @Override
    public Car findById(UUID id) {
        return repository.findByIdAndRemovedFalse(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new DomainValidationException("Entity not found: " + id));
    }

    @Override
    public List<Car> findAll() {
        return repository.findAllByRemovedFalse().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Car> findAll(CarFilter filter) {
        return repository.findAll(CarSpecifications.byFilter(filter)).stream()
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
}
