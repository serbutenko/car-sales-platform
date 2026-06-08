package ru.butenko.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.butenko.domain.enums.UserRole;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class User {
    private final UUID id;
    private final String name;
    private final UserRole role;
}
