package ru.butenko.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.butenko.application.abstractions.UserRepository;
import ru.butenko.domain.exception.DomainValidationException;
import ru.butenko.domain.model.User;
import ru.butenko.persistence.mapper.UserEntityMapper;
import ru.butenko.persistence.repository.SpringDataUserRepository;

import java.util.List;
import java.util.UUID;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RequiredArgsConstructor
@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository repository;
    private final UserEntityMapper mapper;

    @Override
    public User save(User entity) {
        return mapper.toDomain(repository.save(mapper.toEntity(entity)));
    }

    @Override
    public User findById(UUID uuid) {
        return repository.findById(uuid)
                .filter(e -> !e.isRemoved())
                .map(mapper::toDomain)
                .orElseThrow(() -> new DomainValidationException("Entity not found: " + id));
    }

    @Override
    public List<User> findAll() {
        return repository.findAllByRemovedFalse().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID uuid) {
        var entity = repository.findById(uuid)
                .orElseThrow(() -> new DomainValidationException("Entity not found: " + id));
        entity.setRemoved(true);
        repository.save(entity);
    }


}
