package ru.butenko.api.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.butenko.api.dto.response.CustomOrderResponse;
import ru.butenko.api.dto.response.StockOrderResponse;
import ru.butenko.domain.orders.custom.CustomOrder;
import ru.butenko.domain.orders.stock.StockOrder;

@Component
public class OrderDtoMapper {
    private final ConfiguratorDtoMapper configuratorDtoMapper;

    public OrderDtoMapper(ConfiguratorDtoMapper configuratorDtoMapper) {
        this.configuratorDtoMapper = configuratorDtoMapper;
    }

    public StockOrderResponse toResponse(StockOrder order) {
        return new StockOrderResponse(
                order.getId(),
                order.getClientId(),
                order.getManagerId(),
                order.getCarId(),
                order.getStatus()
        );
    }

    public CustomOrderResponse toResponse(CustomOrder order) {
        return new CustomOrderResponse(
                order.getId(),
                order.getClientId(),
                order.getManagerId(),
                order.getModelId(),
                configuratorDtoMapper.toResponse(order.getConfiguration()),
                order.getStatus()
        );
    }
}
