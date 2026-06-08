package ru.butenko.application.abstractions;

import ru.butenko.domain.orders.stock.StockOrder;

import java.util.List;
import java.util.UUID;

public interface StockOrderRepository extends BaseRepository<UUID, StockOrder> {
    List<StockOrder> findAll();

    List<StockOrder> findAllByClientId(UUID clientId);
}
