package ru.butenko.application.abstractions;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<ID, T> {
    T save(T entity);

    T findById(ID id);

    List<T> findAll();

    void deleteById(ID id);
}
