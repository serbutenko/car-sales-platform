package ru.butenko.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.butenko.application.abstractions.CarModelRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.CarModel;
import ru.butenko.persistence.mapper.CarModelEntityMapper;
import ru.butenko.persistence.repository.SpringDataCarModelRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class JpaCarModelRepositoryAdapter implements CarModelRepository {

    private final SpringDataCarModelRepository repository;
    private final CarModelEntityMapper mapper;


    @Override
    public CarModel save(CarModel entity) {
        return mapper.toDomain(repository.save(mapper.toEntity(entity)));
    }

    @Override
    public CarModel findById(UUID id) {
        return repository.findByIdAndRemovedFalse(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new DomainValidationException("Entity not found: " + id));
    }

    @Override
    public List<CarModel> findAll() {
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
}
