package ru.butenko.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.butenko.persistence.entity.StockOrderEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataStockOrderRepository extends JpaRepository<StockOrderEntity, UUID> {

    Optional<StockOrderEntity> findByIdAndRemovedFalse(UUID id);

    List<StockOrderEntity> findAllByRemovedFalse();

    List<StockOrderEntity> findAllByClientIdAndRemovedFalse(UUID clientId);
}
