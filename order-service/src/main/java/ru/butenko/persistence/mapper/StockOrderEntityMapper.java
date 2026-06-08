package ru.butenko.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.butenko.domain.orders.stock.StockOrder;
import ru.butenko.persistence.entity.StockOrderEntity;

@RequiredArgsConstructor
@Component
public class StockOrderEntityMapper {

    private final StockOrderStateFactory stateFactory;

    public StockOrder toDomain(StockOrderEntity entity) {
        return new StockOrder(
                entity.getId(),
                entity.getClientId(),
                entity.getManagerId(),
                entity.getCarId(),
                stateFactory.restoreState(entity.getStatus())
        );
    }

    public StockOrderEntity toEntity(StockOrder domain) {
        StockOrderEntity entity = new StockOrderEntity();
        entity.setId(domain.getId());
        entity.setClientId(domain.getClientId());
        entity.setManagerId(domain.getManagerId());
        entity.setCarId(domain.getCarId());
        entity.setStatus(domain.getStatus());
        return entity;
    }
}
