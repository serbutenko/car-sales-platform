package ru.butenko.application.abstractions;

import ru.butenko.domain.model.User;

import java.util.UUID;

public interface UserRepository extends BaseRepository<UUID, User> {
}
