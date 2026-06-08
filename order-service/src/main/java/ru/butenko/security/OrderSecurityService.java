package ru.butenko.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.butenko.application.abstractions.CustomOrderRepository;
import ru.butenko.application.abstractions.StockOrderRepository;

import java.util.UUID;


@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurityService {

    private final StockOrderRepository stockOrderRepository;
    private final CustomOrderRepository customOrderRepository;
    private final CurrentUserService currentUserService;

    public boolean isStockOrderOwner(UUID orderId) {
        var order = stockOrderRepository.findById(orderId);

        return order.getClientId().equals(currentUserService.getCurrentUserId());
    }

    public boolean isCustomOrderOwner(UUID orderId) {
        var order = customOrderRepository.findById(orderId);

        return order.getClientId().equals(currentUserService.getCurrentUserId());
    }
}
