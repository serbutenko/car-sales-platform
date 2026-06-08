package ru.butenko.api.dto.response;

import java.util.Set;
import java.util.UUID;

public record TestDriveCarsResponse(Set<UUID> cars) {
}
