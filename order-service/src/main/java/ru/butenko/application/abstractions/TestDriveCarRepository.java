package ru.butenko.application.abstractions;

import java.util.List;
import java.util.UUID;

public interface TestDriveCarRepository {

    void add(UUID carId);

    void remove(UUID carId);

    boolean exists(UUID carId);

    List<UUID> findAllCarIds();
}
