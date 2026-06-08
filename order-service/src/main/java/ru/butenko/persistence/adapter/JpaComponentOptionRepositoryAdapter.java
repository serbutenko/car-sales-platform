package ru.butenko.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.butenko.application.abstractions.ComponentOptionRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.ComponentOption;
import ru.butenko.persistence.mapper.ComponentOptionEntityMapper;
import ru.butenko.persistence.repository.SpringDataComponentOptionRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class JpaComponentOptionRepositoryAdapter implements ComponentOptionRepository {

    private final SpringDataComponentOptionRepository repository;
    private final ComponentOptionEntityMapper mapper;

    @Override
    public ComponentOption save(ComponentOption entity) {
        return mapper.toDomain(repository.save(mapper.toEntity(entity)));
    }

    @Override
    public ComponentOption findById(UUID id) {
        return repository.findByIdAndRemovedFalse(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new DomainValidationException("Entity not found: " + id));
    }

    @Override
    public List<ComponentOption> findAll() {
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
