package ru.butenko.application.abstractions;

import ru.butenko.domain.orders.custom.CustomOrder;

import java.util.List;
import java.util.UUID;

public interface CustomOrderRepository extends BaseRepository<UUID, CustomOrder> {
    List<CustomOrder> findAll();

    List<CustomOrder> findAllByClientId(UUID clientId);
}
