package ru.butenko.application.service;

import ru.butenko.application.abstractions.Detail;
import ru.butenko.domain.model.CarModel;

import java.math.BigDecimal;
import java.util.Collection;

public class PriceCalculator {
    public BigDecimal calculate(CarModel carModel, Collection<Detail> details) {
        BigDecimal total = details.stream()
                .map(Detail::getPriceDelta)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return carModel.getPrice().add(total);
    }
}
