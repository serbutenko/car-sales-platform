package ru.butenko;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.butenko.domain.model.CarFilter;
import ru.butenko.persistence.entity.CarEntity;
import ru.butenko.persistence.repository.SpringDataCarRepository;
import ru.butenko.persistence.specification.CarSpecifications;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class CarRepositoryIT extends BaseIT {

    @Autowired
    private SpringDataCarRepository carRepository;

    @Test
    void shouldFindSeedCarBySpecificationFilter() {
        CarFilter filter = new CarFilter();
        filter.setBrand("BMW");
        filter.setComponentOptionIds(List.of(
                UUID.fromString("44444444-4444-4444-4444-444444444441"),
                UUID.fromString("44444444-4444-4444-4444-444444444442")
        ));
        List<CarEntity> cars = carRepository.findAll(CarSpecifications.byFilter(filter));
        assertFalse(cars.isEmpty());
        assertEquals("BMW", cars.getFirst().getBrand());
        assertEquals("320i", cars.getFirst().getModelName());
    }
}
